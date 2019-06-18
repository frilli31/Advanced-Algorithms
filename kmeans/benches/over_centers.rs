#[macro_use]
extern crate criterion;
#[macro_use]
extern crate lazy_static;
extern crate kmeans;

use criterion::{Criterion, ParameterizedBenchmark};

use kmeans::{get_sorted_cities, parallel_k_means, serial_k_means, City};

fn bench_fibs(c: &mut Criterion) {
    lazy_static! {
        static ref CITIES: Vec<City> = get_sorted_cities("cities-and-towns-of-usa.csv");
    }

    let all: Vec<u32> = (10..=100).step_by(10).collect();

    //all.append(&mut (10..=100).step_by(10).collect());

    c.bench(
        "over_centers",
        ParameterizedBenchmark::new(
            "Serial",
            |b, i| b.iter(|| serial_k_means(&*CITIES, *i, 100)),
            all,
        )
        .with_function("Parallel", |b, i| {
            b.iter(|| parallel_k_means(&*CITIES, *i, 100, 1))
        })
        .sample_size(3)
        .warm_up_time(std::time::Duration::from_secs(1)),
    );
}

criterion_group!(benches, bench_fibs);
criterion_main!(benches);
