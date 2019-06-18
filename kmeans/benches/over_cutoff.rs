#[macro_use]
extern crate criterion;
#[macro_use]
extern crate lazy_static;
extern crate kmeans;

use criterion::{Criterion, ParameterizedBenchmark};

use kmeans::{get_sorted_cities, parallel_k_means, City};

fn bench_fibs(c: &mut Criterion) {
    lazy_static! {
        static ref CITIES: Vec<City> = get_sorted_cities("cities-and-towns-of-usa.csv");
    }

    let mut cutoffs = Vec::new();
    let mut i = 1;
    while i < CITIES.len() {
        cutoffs.push(i);
        i = i * 2;
    }
    cutoffs.push(CITIES.len());

    c.bench(
        "over_interactions",
        ParameterizedBenchmark::new(
            "Parallel",
            |b, i| b.iter(|| parallel_k_means(&*CITIES, 50, 100, *i)),
            cutoffs,
        )
        .sample_size(3)
        .warm_up_time(std::time::Duration::from_secs(1)),
    );
}

criterion_group!(benches, bench_fibs);
criterion_main!(benches);
