import ReactApexChart from 'react-apexcharts';
import React from 'react';
import ReactDOM from 'react-dom';
import { parse } from 'ipaddr.js';
// import data from './chartdata';

var newArr = [];



class ApexChart extends React.Component {
    constructor(props) {
        super(props);

        console.info(this.props.barinfo);

        this.state = {
            series: [{
                data: newArr
            }],
            options: {
                chart: {
                    type: 'candlestick',
                    height: 350
                },
                title: {
                    text: 'CandleStick Chart',
                    align: 'left'
                },
                xaxis: {
                    type: 'datetime'
                },
                yaxis: {
                    tooltip: {
                        enabled: true
                    }
                },
                // dataLabels: {
                //     enabled: false
                // },
                annotations: {
                    points: 
                    [
                        {
                            x: new Date(1538843400000).getTime(),
                            y: 6620.7,
                            marker: {
                                size: 8,
                            },
                            label: {
                                borderColor: '#FF4560',
                                text: "href=google.com"
                            }
                        }
                    ],
                },
            },
        };

    }

        
    parseData(toParse){
    // var barss = { "bars":[{"t":1632240900,"o":144.295,"h":144.34,"l":144.06,"c":144.06,"v":17603},{"t":1632241800,"o":144.04,"h":144.205,"l":143.95,"c":144.16,"v":7177},{"t":1632242700,"o":144.185,"h":144.255,"l":143.75,"c":143.87,"v":9601},{"t":1632243600,"o":143.88,"h":143.94,"l":143.72,"c":143.83,"v":8732},{"t":1632244500,"o":143.8,"h":143.9,"l":143.47,"c":143.47,"v":15387},{"t":1632245400,"o":143.515,"h":143.53,"l":143.34,"c":143.38,"v":10676},{"t":1632246300,"o":143.38,"h":143.73,"l":143.3,"c":143.46,"v":8762},{"t":1632247200,"o":143.68,"h":143.68,"l":143.45,"c":143.48,"v":9233},{"t":1632248100,"o":143.42,"h":143.495,"l":143.2,"c":143.22,"v":10669},{"t":1632249000,"o":143.21,"h":143.59,"l":143.21,"c":143.55,"v":11161},{"t":1632249900,"o":143.53,"h":144.12,"l":143.5,"c":143.87,"v":22282},{"t":1632250800,"o":143.855,"h":144.11,"l":143.85,"c":144.08,"v":20295},{"t":1632251700,"o":144.1,"h":144.22,"l":143.89,"c":144.05,"v":24019},{"t":1632252600,"o":144.12,"h":144.21,"l":143.89,"c":144.025,"v":28661},{"t":1632253500,"o":143.96,"h":143.97,"l":143.405,"c":143.405,"v":83053},{"t":1632254400,"o":143.35,"h":143.35,"l":143.13,"c":143.15,"v":813},{"t":1632255300,"o":143.15,"h":143.16,"l":143.15,"c":143.16,"v":200},{"t":1632256200,"o":143.17,"h":143.17,"l":143.17,"c":143.17,"v":105},{"t":1632317400,"o":144.44,"h":144.63,"l":143.71,"c":144.03,"v":53109},{"t":1632318300,"o":143.99,"h":144.54,"l":143.97,"c":144.5,"v":34739},{"t":1632319200,"o":144.62,"h":145.2,"l":144.62,"c":144.985,"v":34556},{"t":1632320100,"o":145.03,"h":145.29,"l":144.96,"c":145.095,"v":32743},{"t":1632321000,"o":145.1,"h":145.215,"l":144.93,"c":145.07,"v":35327},{"t":1632321900,"o":145.095,"h":145.15,"l":144.89,"c":144.925,"v":67647},{"t":1632322800,"o":145.01,"h":145.22,"l":145.01,"c":145.15,"v":22090},{"t":1632323700,"o":145.155,"h":145.23,"l":145.03,"c":145.115,"v":16576},{"t":1632324600,"o":145.09,"h":145.49,"l":145.09,"c":145.465,"v":11880},{"t":1632325500,"o":145.48,"h":145.88,"l":145.36,"c":145.54,"v":25814},{"t":1632326400,"o":145.515,"h":145.6,"l":145.39,"c":145.51,"v":11709},{"t":1632327300,"o":145.5,"h":145.605,"l":145.45,"c":145.5,"v":15284},{"t":1632328200,"o":145.52,"h":145.68,"l":145.3,"c":145.3,"v":11692},{"t":1632329100,"o":145.31,"h":145.35,"l":145.205,"c":145.23,"v":5112},{"t":1632330000,"o":145.25,"h":145.25,"l":145.1,"c":145.14,"v":9865},{"t":1632330900,"o":145.13,"h":145.31,"l":145.06,"c":145.285,"v":14468},{"t":1632331800,"o":145.27,"h":145.38,"l":145.16,"c":145.18,"v":11980},{"t":1632332700,"o":145.17,"h":145.285,"l":145.045,"c":145.285,"v":18337},{"t":1632333600,"o":144.85,"h":146.17,"l":144.785,"c":146.11,"v":76212},{"t":1632334500,"o":146.24,"h":146.41,"l":145.83,"c":146.12,"v":43182},{"t":1632335400,"o":146.09,"h":146.14,"l":145.47,"c":145.75,"v":38008},{"t":1632336300,"o":145.65,"h":145.7,"l":145.07,"c":145.34,"v":41163},{"t":1632337200,"o":145.345,"h":145.92,"l":145.24,"c":145.86,"v":40966},{"t":1632338100,"o":145.83,"h":146.43,"l":145.79,"c":146.175,"v":50088},{"t":1632339000,"o":146.15,"h":146.22,"l":145.79,"c":145.845,"v":47495},{"t":1632339900,"o":145.85,"h":146.24,"l":145.7,"c":145.82,"v":188967},{"t":1632341700,"o":145.99,"h":145.99,"l":145.99,"c":145.99,"v":100},{"t":1632403800,"o":146.73,"h":146.75,"l":145.66,"c":146.375,"v":86329},{"t":1632404700,"o":146.37,"h":146.63,"l":146.2,"c":146.25,"v":32573},{"t":1632405600,"o":146.305,"h":146.88,"l":146.255,"c":146.815,"v":39334},{"t":1632406500,"o":146.88,"h":146.97,"l":146.67,"c":146.8,"v":23317},{"t":1632407400,"o":146.79,"h":146.82,"l":146.58,"c":146.62,"v":16768},{"t":1632408300,"o":146.685,"h":147.08,"l":146.665,"c":146.96,"v":34555},{"t":1632409200,"o":146.935,"h":146.97,"l":146.6,"c":146.77,"v":26179},{"t":1632410100,"o":146.805,"h":146.84,"l":146.37,"c":146.66,"v":21343},{"t":1632411000,"o":146.63,"h":146.875,"l":146.575,"c":146.605,"v":14025},{"t":1632411900,"o":146.65,"h":146.94,"l":146.55,"c":146.895,"v":10108},{"t":1632412800,"o":146.92,"h":146.93,"l":146.51,"c":146.57,"v":19320},{"t":1632413700,"o":146.605,"h":146.7,"l":146.53,"c":146.53,"v":15632},{"t":1632414600,"o":146.61,"h":146.77,"l":146.6,"c":146.64,"v":7844},{"t":1632415500,"o":146.67,"h":146.78,"l":146.59,"c":146.6,"v":8374},{"t":1632416400,"o":146.575,"h":146.82,"l":146.555,"c":146.64,"v":15771},{"t":1632417300,"o":146.64,"h":146.915,"l":146.64,"c":146.915,"v":8772},{"t":1632418200,"o":146.92,"h":146.96,"l":146.86,"c":146.94,"v":11788},{"t":1632419100,"o":146.97,"h":147.02,"l":146.9,"c":147.02,"v":9461},{"t":1632420000,"o":146.99,"h":147,"l":146.81,"c":146.87,"v":12087},{"t":1632420900,"o":146.87,"h":146.89,"l":146.72,"c":146.76,"v":9222},{"t":1632421800,"o":146.765,"h":146.9,"l":146.72,"c":146.73,"v":14209},{"t":1632422700,"o":146.79,"h":146.98,"l":146.73,"c":146.95,"v":9629},{"t":1632423600,"o":146.945,"h":146.945,"l":146.71,"c":146.73,"v":10290},{"t":1632424500,"o":146.74,"h":146.905,"l":146.71,"c":146.8,"v":33341},{"t":1632425400,"o":146.81,"h":146.93,"l":146.745,"c":146.885,"v":44562},{"t":1632426300,"o":146.895,"h":146.97,"l":146.725,"c":146.9,"v":177130},{"t":1632427200,"o":146.84,"h":146.84,"l":146.84,"c":146.84,"v":100},{"t":1632429000,"o":146.74,"h":146.77,"l":146.74,"c":146.76,"v":400},{"t":1632489300,"o":145.79,"h":145.79,"l":145.79,"c":145.79,"v":100},{"t":1632490200,"o":145.63,"h":146.28,"l":145.57,"c":146.13,"v":52202},{"t":1632491100,"o":146.16,"h":146.21,"l":145.79,"c":146.17,"v":32852},{"t":1632492000,"o":146.2,"h":146.385,"l":145.84,"c":145.91,"v":24228},{"t":1632492900,"o":145.845,"h":146.23,"l":145.765,"c":146.11,"v":28608},{"t":1632493800,"o":146.16,"h":146.32,"l":146,"c":146.085,"v":28661},{"t":1632494700,"o":146.075,"h":146.075,"l":145.75,"c":145.865,"v":26071},{"t":1632495600,"o":145.91,"h":146.195,"l":145.9,"c":146.065,"v":17681},{"t":1632496500,"o":146.05,"h":146.155,"l":145.87,"c":145.93,"v":18054},{"t":1632497400,"o":145.94,"h":146.09,"l":145.88,"c":146.035,"v":14910},{"t":1632498300,"o":146.05,"h":146.425,"l":146.02,"c":146.425,"v":13655},{"t":1632499200,"o":146.425,"h":146.63,"l":146.295,"c":146.57,"v":13142},{"t":1632500100,"o":146.575,"h":146.64,"l":146.49,"c":146.6,"v":14658},{"t":1632501000,"o":146.58,"h":146.68,"l":146.445,"c":146.68,"v":16417},{"t":1632501900,"o":146.715,"h":146.78,"l":146.55,"c":146.61,"v":7477},{"t":1632502800,"o":146.6,"h":146.7,"l":146.5,"c":146.63,"v":7704},{"t":1632503700,"o":146.625,"h":146.77,"l":146.61,"c":146.74,"v":8458},{"t":1632504600,"o":146.785,"h":146.88,"l":146.61,"c":146.88,"v":16298},{"t":1632505500,"o":146.88,"h":146.89,"l":146.705,"c":146.71,"v":12808},{"t":1632506400,"o":146.77,"h":146.83,"l":146.705,"c":146.71,"v":11278},{"t":1632507300,"o":146.72,"h":146.87,"l":146.72,"c":146.85,"v":11548},{"t":1632508200,"o":146.83,"h":146.895,"l":146.78,"c":146.84,"v":11803},{"t":1632509100,"o":146.85,"h":147.03,"l":146.85,"c":147.02,"v":16897},{"t":1632510000,"o":147,"h":147,"l":146.825,"c":146.92,"v":11803},{"t":1632510900,"o":146.97,"h":147.28,"l":146.96,"c":147.265,"v":31925},{"t":1632511800,"o":147.27,"h":147.465,"l":147.2,"c":147.3,"v":42970},{"t":1632512700,"o":147.285,"h":147.35,"l":146.83,"c":147.03,"v":127585}]}

    //   fetch("http://localhost/stocks")
    //   .then(res => res.json())
    //   .then(data => dataa = data)
    //   .then(() => console.log(dataa)
    // );
    
    //Fethch 'then' has to be used in conjunction with use effect. Since fetch is async, I have to find a solution so that it reloads the chart with the newly fetched data
    
    
    // const jsonString = JSON.stringify(chartdataa);
    // console.log(JSON.stringify(chartdataa));
    // const parsedJson = JSON.parse(chartdataa);
    
    //parse json data into array
//     for (let i in toParse) {
//         const day = toParse[i];
//         newArr[i] = {"x":new Date(day.t), "y":[day.o,day.h,day.l,day.c]};
//         console.log(newArr[i]);
//    }
}
    
    render() {
        
        this.state.data = newArr;
        return (
            <div id="chart">
            <ReactApexChart options={this.state.options} series={this.state.series} type="candlestick" height={800} />
            </div>
            );
        }
    }
    
    const domContainer = document.querySelector('#root');
    ReactDOM.render(React.createElement(ApexChart), domContainer);
    
    export default ApexChart;
    
    
    