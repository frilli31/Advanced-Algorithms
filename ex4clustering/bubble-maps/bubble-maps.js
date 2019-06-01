(async function() {
  const dataset = await d3.csv('kmeans.csv', d => {
    return {
      x: Number(d.x),
      y: Number(d.y),
      population: Number(d.population),
      cluster: Number(d.cluster),
      centroid: d.centroid,
    };
  });
  const centroids = Array.from(new Set(dataset.map(county => county.centroid))).map(centroid =>
    centroid.split(';').map(Number),
  );

  /**
   * Mike Bostock's margin convention
   * @see {@link https://bl.ocks.org/mbostock/3019563}
   */
  const margin = { top: 0, right: 0, bottom: 0, left: 0 };
  const width = 1000 - margin.left - margin.right;
  const height = 734 - margin.top - margin.bottom;
  const svg = d3
    .select('.bubble-maps')
    .append('svg')
    .style('width', width + margin.left + margin.right)
    .style('height', height + margin.top + margin.bottom)
    .append('g')
    .attr('transform', `translate(${margin.left}, ${margin.top})`);

  const xScale = d3
    .scaleLinear()
    .domain([0, 1000])
    .range([0, width]);
  const yScale = d3
    .scaleLinear()
    .domain([0, 734])
    .range([0, height]);
  const radiusScale = d3
    .scaleSqrt()
    .domain(d3.extent(dataset, d => d.population))
    .range([0, 20]);
  const designedColors = [
    '#267278',
    '#65338d',
    '#4770b3',
    '#d21f75',
    '#3b3689',
    '#00BED1',
    '#48b24f',
    '#e57438',
    '#569dd2',
    '#8D5649',
    '#58595b',
    '#e4b031',
    '#84d2f4',
    '#cad93f',
    '#f5c8af',
    '#9ac483',
    '#9e9ea2',
  ];
  const formatAsThousands = d3.format(',.2r');

  const counties = svg
    .selectAll('circle')
    .data(dataset)
    .enter()
    .append('circle')
    .attr('cx', d => xScale(d.x))
    .attr('cy', d => yScale(d.y))
    .attr('r', d => radiusScale(d.population))
    .attr('fill', d => designedColors[d.cluster])
    .attr('fill-opacity', 0.5)
    .attr('stroke', d => designedColors[d.cluster]);

  counties.on('mouseover', function(d) {
    const circle = d3.select(this);
    const x = Number(circle.attr('cx'));
    const y = Number(circle.attr('cy')) - 10;

    svg
      .append('text')
      .attr('id', 'tooltip')
      .attr('x', x)
      .attr('y', y)
      .attr('text-anchor', 'middle')
      .attr('font-size', '11px')
      .text(formatAsThousands(d.population));
  });
  counties.on('mouseout', () => {
    svg.select('#tooltip').remove();
  });

  svg
    .selectAll('path')
    .data(centroids)
    .enter()
    .append('path')
    .attr('transform', d => `translate(${xScale(d[0])}, ${yScale(d[1])})`)
    .attr('d', d3.symbol().type(d3.symbolTriangle))
    .attr('fill', (d, i) => designedColors[i]);

  // Legend
  svg
    .append('path')
    .attr('transform', () => `translate(30, 690)`)
    .attr(
      'd',
      d3
        .symbol()
        .size(200)
        .type(d3.symbolTriangle),
    )
    .attr('fill', '#666');
  svg
    .append('text')
    .text('Cluster centroid')
    .attr('transform', `translate(50, 693)`)
    .style('font-size', '13px');
})();
