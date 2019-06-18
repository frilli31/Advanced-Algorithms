extern crate regex;

use rayon::iter::IndexedParallelIterator;
use rayon::iter::IntoParallelIterator;
use rayon::iter::ParallelIterator;
use regex::Regex;

const R: f64 = 6_378.388;

#[allow(clippy::approx_constant)]
const PI: f64 = 3.141_592;

#[derive(Clone, Debug)]
pub struct City {
    pub latitude: f64,
    pub longitude: f64,
    pub population: i32,
}

pub fn get_sorted_cities(file_name: &str) -> Vec<City> {
    let mut cities = parse_cities(file_name);
    cities.sort_by(|one, two| two.population.cmp(&one.population));
    cities
}

pub fn parse_cities(file_name: &str) -> Vec<City> {
    let content = std::fs::read_to_string(file_name).unwrap().replace(" ", "");
    let re_of_city = Regex::new("\n(?:\\d+),(?:[^,]+),([^,]+),([^,]+),(\\S*)").unwrap();
    re_of_city
        .captures_iter(&content)
        .map(|cap| City {
            population: cap[1].parse().unwrap(),
            latitude: cap[2].parse().unwrap(),
            longitude: cap[3].parse().unwrap(),
        })
        .collect()
}

pub fn parallel_k_means(
    cities: &[City],
    number_of_centers: u32,
    interactions: u32,
    cutoff: usize,
) -> Vec<Vec<&City>> {
    let size = cities.len();

    let mut initial_clusters: Vec<_> = cities
        .iter()
        .take(number_of_centers as usize)
        .map(|city| (city.latitude, city.longitude))
        .collect();

    let mut cluster_of_cities: Vec<u32> = vec![0; size];

    for _interaction in 0..interactions {
        {
            let initial_clusters = &initial_clusters; // faccio il borrow immutabile in modo da poterla condividere tra più thread con il par_iter
            cities.into_par_iter().zip(&mut cluster_of_cities).for_each(
                |(city, cluster_of_city)| {
                    let (index, _distance) = initial_clusters
                        .iter()
                        .map(|cluster| distance(cluster, &(city.latitude, city.longitude)))
                        .enumerate()
                        .min_by(|one, two| one.1.cmp(&two.1))
                        .unwrap();
                    *cluster_of_city = index as u32;
                },
            );
        }

        (0..number_of_centers)
            .into_par_iter()
            .zip(&mut initial_clusters)
            .for_each(|(index, cluster)| {
                let (size, sum_of_lat, sum_of_long) =
                    parallel_cluster_reduce(&cluster_of_cities, &cities, index as u32, cutoff);
                if size != 0 {
                    *cluster = (sum_of_lat / size as f64, sum_of_long / size as f64)
                }
            });
    }

    let mut result: Vec<Vec<&City>> = vec![Vec::with_capacity(size); number_of_centers as usize];
    cluster_of_cities
        .iter()
        .zip(cities.iter())
        .for_each(|(cluster, city)| result[*cluster as usize].push(city));
    result
}

fn distance(one: &(f64, f64), two: &(f64, f64)) -> u32 {
    let one_rad = (coordinates_to_radians(one.0), coordinates_to_radians(one.1));
    let two_rad = (coordinates_to_radians(two.0), coordinates_to_radians(two.1));

    let q1 = (one_rad.1 - two_rad.1).cos();
    let q2 = (one_rad.0 - two_rad.0).cos();
    let q3 = (one_rad.0 + two_rad.0).cos();

    (R * (0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)).acos() + 1.0).trunc() as u32
}

fn coordinates_to_radians(coordinate: f64) -> f64 {
    let deg = coordinate.trunc();
    PI * (deg + 5.0 * (coordinate - deg) / 3.0) / 180.0
}

fn sequential_cluster_reduce(
    cluster_of_cities: &[u32],
    cities: &[City],
    h: u32,
) -> (usize, f64, f64) {
    cluster_of_cities
        .iter()
        .zip(cities.iter())
        .filter(|(cluster, _)| **cluster == h)
        .fold((0, 0f64, 0f64), |acc, (_, city)| {
            (acc.0 + 1, acc.1 + city.latitude, acc.2 + city.longitude)
        })
}

fn parallel_cluster_reduce(
    cluster_of_cities: &[u32],
    cities: &[City],
    h: u32,
    cutoff: usize,
) -> (usize, f64, f64) {
    let size = cluster_of_cities.len();

    if size <= cutoff {
        sequential_cluster_reduce(cluster_of_cities, cities, h)
    } else {
        let mid = size / 2;

        let (one, two) = rayon::join(
            || parallel_cluster_reduce(&cluster_of_cities[0..mid], &cities[0..mid], h, cutoff),
            || {
                parallel_cluster_reduce(
                    &cluster_of_cities[mid..size],
                    &cities[mid..size],
                    h,
                    cutoff,
                )
            },
        );
        (one.0 + two.0, one.1 + two.1, one.2 + two.2)
    }
}

pub fn serial_k_means(
    cities: &[City],
    number_of_centers: u32,
    interactions: u32,
) -> Vec<Vec<&City>> {
    let size = cities.len();

    let mut initial_clusters: Vec<_> = cities
        .iter()
        .take(number_of_centers as usize)
        .map(|city| ((city.latitude, city.longitude), Vec::new()))
        .collect();

    for _interaction in 0..interactions {
        initial_clusters
            .iter_mut()
            .for_each(|cluster| cluster.1 = Vec::with_capacity(size));

        cities.iter().for_each(|city| {
            let (index, _distance) = initial_clusters
                .iter()
                .map(|cluster| distance(&cluster.0, &(city.latitude, city.longitude)))
                .enumerate()
                .min_by(|one, two| one.1.cmp(&two.1))
                .unwrap();
            initial_clusters[index].1.push(city);
        });

        initial_clusters.iter_mut().for_each(|cluster| {
            let size = cluster.1.len();
            if size != 0 {
                let (sum_lat, sum_long) = cluster.1.iter().fold((0f64, 0f64), |acc, city| {
                    (acc.0 + city.latitude, acc.1 + city.longitude)
                });
                cluster.0 = (sum_lat / size as f64, sum_long / size as f64);
            }
        });
    }
    initial_clusters
        .into_iter()
        .map(|(_, cities)| cities)
        .collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_parse_cities() {
        assert_eq!(parse_cities("cities-and-towns-of-usa.csv").len(), 38_183);
    }
}
