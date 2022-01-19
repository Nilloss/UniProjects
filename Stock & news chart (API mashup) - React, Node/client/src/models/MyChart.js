import { createChart } from 'lightweight-charts';

class MyChart {

		constructor(Document) {
			var chart = createChart(this.element, { width: 400, height: 300 });

			const lineSeries = chart.addCandlestickSeries({
				upColor: 'rgba(255, 144, 0, 1)',
				downColor: '#000',
				borderDownColor: 'rgba(255, 144, 0, 1)',
				borderUpColor: 'rgba(255, 144, 0, 1)',
				wickDownColor: 'rgba(255, 144, 0, 1)',
				wickUpColor: 'rgba(255, 144, 0, 1)',
			});
			
			var data = [    { time: '2018-10-19', open: 180.34, high: 180.99, low: 178.57, close: 179.85 },
			{ time: '2018-10-22', open: 180.82, high: 181.40, low: 177.56, close: 178.75 },
			{ time: '2018-10-23', open: 175.77, high: 179.49, low: 175.44, close: 178.53 },
			{ time: '2018-10-24', open: 178.58, high: 182.37, low: 176.31, close: 176.97 },
			{ time: '2018-10-25', open: 177.52, high: 180.50, low: 176.83, close: 179.07 },
			{ time: '2018-10-26', open: 176.88, high: 177.34, low: 170.91, close: 172.23 },
			{ time: '2018-10-29', open: 173.74, high: 175.99, low: 170.95, close: 173.20 },];

			lineSeries.setData(data);
		}
}


