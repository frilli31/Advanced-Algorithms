#[macro_use]
extern crate criterion;
extern crate kmeans;

use std::fmt;
use std::iter::repeat;

use criterion::{Criterion, ParameterizedBenchmark};

use kmeans::{get_sorted_cities, parallel_k_means, serial_k_means, City};

struct Cities(Vec<City>);

impl fmt::Debug for Cities {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{}", self.0.len())
    }
}

fn bench_fibs(c: &mut Criterion) {
    let cities_all = get_sorted_cities("cities-and-towns-of-usa.csv");
    let cutoffs = vec![100_000, 50_000, 15_000, 5_000, 2_000, 250, std::i32::MIN];

    let data_sets: Vec<Cities> = repeat(cities_all.into_iter())
        .zip(cutoffs)
        .map(|(cities, cutof)| cities.filter(|city| city.population > cutof).collect())
        .map(|v| Cities(v))
        .collect();

    c.bench(
        "over_cities",
        ParameterizedBenchmark::new(
            "Serial",
            |b, i| b.iter(|| serial_k_means(i.0.as_ref(), 50, 100)),
            data_sets,
        )
        .with_function("Parallel", |b, i| {
            b.iter(|| parallel_k_means(i.0.as_ref(), 50, 100, 1))
        })
        .sample_size(2)
        .warm_up_time(std::time::Duration::from_secs(1)),
    );
}

criterion_group!(benches, bench_fibs);
criterion_main!(benches);
