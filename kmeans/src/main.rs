use kmeans::*;
use std::time::SystemTime;

fn main() {
    let cities = get_sorted_cities("cities-and-towns-of-usa.csv");

    let start = SystemTime::now();
    let _result = parallel_k_means(&cities, 100, 100, 100_000);
    println!("{:?}", start.elapsed());
    _result
        .iter()
        .for_each(|cluster| print!("{:?} ", cluster.len()));

    let start = SystemTime::now();
    let _result = serial_k_means(&cities, 100, 100);
    println!("{:?}", start.elapsed());
    _result
        .iter()
        .for_each(|cluster| print!("{:?} ", cluster.len()));
}
