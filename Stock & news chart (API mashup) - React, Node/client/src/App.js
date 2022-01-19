import React, { useState, useEffect } from "react";

import ReactApexChart from 'react-apexcharts';


function App() {
  // const [search, setSearch] = useState("");

  const [search, setSearch] = useState("");
  const urloriginal = "/chartdata/";
  const [data, setData] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    let response = await fetch(urloriginal + search);
    setData(await response.json());
    console.log(urloriginal + search);
    console.info(data)
  }

  var chartseries = [{
    data: data.bars
  }];

  var chartoptions = {
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
    dataLabels: {
      enabled: false
    },
    annotations: {
      points:
        data.news
    },
  };

  return (
    <div>
      <div className="heading">
        <h1>Newsify</h1>
      </div>

      <form className="field">
        <input type="text" placeholder="search.." onChange={(e) => setSearch(e.target.value)} />

        <input type="submit" value="Search" onClick={(e) => handleSubmit(e)} />
      </form>
      <div id="chart">
        {data == "" ? "nothing to show" : <ReactApexChart options={chartoptions} series={chartseries} type="candlestick" height={800} />}
      </div>
    </div>
  );
}


export default App;
