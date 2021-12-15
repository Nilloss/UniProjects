const path = require('path')
const express = require('express')
const app = express()
const port = 80

const data = require("./services/tradier/data_controller");

// Serve out any static assets correctly
app.use(express.static('../client/build'))


// //Index route
// app.get('/', (req, res) => {
//   res.send("Use url /chartdata/symb");
// })

//------- FETCH PRICE AND NEWS DATA --------
app.get('/chartdata/:symb', (req, res) => {
  console.log(req);
  
  const fetch = require("cross-fetch");

  //Stock tokens
  var keyid = "AKFE7HY7NOY1NFJT547Z";
  var secretkey = "1oglwIvoiXPvK3j3uzJhI2Bgg0m9TIV6dimn1zKi";
  //News tokens
  var token = 'qgtkpibpavghkuazfbxumoppy6v7cpzyyn5mb397';
  //Variables
  var symb = req.params.symb.toString().toUpperCase();

  const priceData = [];
  const newsData = [];

  //------------------Begin stock price request --------------
  const stockurl = new URL("https://data.alpaca.markets/v1/bars/15Min?"),
    params = {
      'symbols': symb,
      'limit': 96,
    }
  Object.keys(params).forEach(key => stockurl.searchParams.append(key, params[key]))
  fetch(stockurl, {
    headers: {
      'APCA-API-KEY-ID': keyid,
      'APCA-API-SECRET-KEY': secretkey,
    }
  })
    .then(response => {
      response.json().then(parsedJsonStocks => {
        var dataStocks = symb == "TEST" ? chartdata['AAPL'] : parsedJsonStocks[symb];

        //Parse stock data for front end chart
        for (let i in dataStocks) {
          const bar = dataStocks[i];
          priceData[i] = { 'x': (new Date(bar.t)).getTime(), "y": [bar.o, bar.h, bar.l, bar.c] };
        }

        //---------------Begin news request------------
        var newsurl = new URL("https://stocknewsapi.com/api/v1"),
          params = {
            'tickers': symb,
            'items': 50,
          }
        Object.keys(params).forEach(key => newsurl.searchParams.append(key, params[key]))

        fetch(newsurl, {
          headers: {
            'Authorization': 'Bearer ' + token,
            'Accept': 'application/json',
          }
        })
          .then(response => {
            response.json().then(parsedJsonNews => {
              var dataNews = symb == "TEST" ? newsdata['data'] : parsedJsonNews['data'];

              //Parse news data
              for (let i in dataNews) {
                const newsitem = dataNews[i];
                const newsTime = new Date(newsitem.date);
                const closestbar = priceData[getClosestBar(newsTime, priceData)];

                newsData[i] = {
                  'x': closestbar.x, 'y': closestbar.y[0],
                  // marker: {
                  //   size: 2,
                  //   shape: 'circle',
                  //   radius: 1
                  // },
                  label: {
                    borderColor: newsitem.sentiment == "Negative" ? '#fc030f' : '#03fc0f',
                    borderRadius: 10,
                    textAnchor: 'middle',
                    backgroundColor: '#000',
                    text: newsitem.text,
                    style: { 
                      fontSize: "8",
                      fontFamily: "Arial, sans-serif"
                    }
                  }
                };
              }

              const result = { 'bars': priceData, 'news': newsData };
              res.json(result);
            })
          });
      })
    });
})

// New api routes should be added here.
// It's important for them to be before the `app.use()` call below as that will match all routes.

// Any routes that don't match on our static assets or api should be sent to the React Application
// This allows for the use of things like React Router
app.use((req, res) => {
  res.sendFile(path.join(__dirname, '../client/build', 'index.html'));
})

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})

//Utility functions
function todayMinus(numdays) {
  //2019-04-15T09:30:00-04:00
  var two = 'T09:30:00-04:00'
  var d = new Date();
  d.setDate(d.getDate() - numdays);
  var one = d.toISOString();
  var subone = one.substring(0, 10);
  var k = subone + two;
  return k;
}

function getClosestBar(testDate, bars) {
  var bestDate = bars.length;
  var bestDiff = -(new Date(0, 0, 0)).valueOf();
  var currDiff = 0;
  var i;

  for (i = 0; i < bars.length; ++i) {
    currDiff = Math.abs(new Date(bars[i].x * 1000) - testDate);
    if (currDiff < bestDiff) {
      bestDate = i;
      bestDiff = currDiff;
    }
  }
  return bestDate;
}


// --------- TEST DATA --------------
var chartdata = {
  "AAPL": [
    {
      "t": 1632240900,
      "o": 144.295,
      "h": 144.34,
      "l": 144.06,
      "c": 144.06,
      "v": 17603
    },
    {
      "t": 1632241800,
      "o": 144.04,
      "h": 144.205,
      "l": 143.95,
      "c": 144.16,
      "v": 7177
    },
    {
      "t": 1632242700,
      "o": 144.185,
      "h": 144.255,
      "l": 143.75,
      "c": 143.87,
      "v": 9601
    },
    {
      "t": 1632243600,
      "o": 143.88,
      "h": 143.94,
      "l": 143.72,
      "c": 143.83,
      "v": 8732
    },
    {
      "t": 1632244500,
      "o": 143.8,
      "h": 143.9,
      "l": 143.47,
      "c": 143.47,
      "v": 15387
    },
    {
      "t": 1632245400,
      "o": 143.515,
      "h": 143.53,
      "l": 143.34,
      "c": 143.38,
      "v": 10676
    },
    {
      "t": 1632246300,
      "o": 143.38,
      "h": 143.73,
      "l": 143.3,
      "c": 143.46,
      "v": 8762
    },
    {
      "t": 1632247200,
      "o": 143.68,
      "h": 143.68,
      "l": 143.45,
      "c": 143.48,
      "v": 9233
    },
    {
      "t": 1632248100,
      "o": 143.42,
      "h": 143.495,
      "l": 143.2,
      "c": 143.22,
      "v": 10669
    },
    {
      "t": 1632249000,
      "o": 143.21,
      "h": 143.59,
      "l": 143.21,
      "c": 143.55,
      "v": 11161
    },
    {
      "t": 1632249900,
      "o": 143.53,
      "h": 144.12,
      "l": 143.5,
      "c": 143.87,
      "v": 22282
    },
    {
      "t": 1632250800,
      "o": 143.855,
      "h": 144.11,
      "l": 143.85,
      "c": 144.08,
      "v": 20295
    },
    {
      "t": 1632251700,
      "o": 144.1,
      "h": 144.22,
      "l": 143.89,
      "c": 144.05,
      "v": 24019
    },
    {
      "t": 1632252600,
      "o": 144.12,
      "h": 144.21,
      "l": 143.89,
      "c": 144.025,
      "v": 28661
    },
    {
      "t": 1632253500,
      "o": 143.96,
      "h": 143.97,
      "l": 143.405,
      "c": 143.405,
      "v": 83053
    },
    {
      "t": 1632254400,
      "o": 143.35,
      "h": 143.35,
      "l": 143.13,
      "c": 143.15,
      "v": 813
    },
    {
      "t": 1632255300,
      "o": 143.15,
      "h": 143.16,
      "l": 143.15,
      "c": 143.16,
      "v": 200
    },
    {
      "t": 1632256200,
      "o": 143.17,
      "h": 143.17,
      "l": 143.17,
      "c": 143.17,
      "v": 105
    },
    {
      "t": 1632317400,
      "o": 144.44,
      "h": 144.63,
      "l": 143.71,
      "c": 144.03,
      "v": 53109
    },
    {
      "t": 1632318300,
      "o": 143.99,
      "h": 144.54,
      "l": 143.97,
      "c": 144.5,
      "v": 34739
    },
    {
      "t": 1632319200,
      "o": 144.62,
      "h": 145.2,
      "l": 144.62,
      "c": 144.985,
      "v": 34556
    },
    {
      "t": 1632320100,
      "o": 145.03,
      "h": 145.29,
      "l": 144.96,
      "c": 145.095,
      "v": 32743
    },
    {
      "t": 1632321000,
      "o": 145.1,
      "h": 145.215,
      "l": 144.93,
      "c": 145.07,
      "v": 35327
    },
    {
      "t": 1632321900,
      "o": 145.095,
      "h": 145.15,
      "l": 144.89,
      "c": 144.925,
      "v": 67647
    },
    {
      "t": 1632322800,
      "o": 145.01,
      "h": 145.22,
      "l": 145.01,
      "c": 145.15,
      "v": 22090
    },
    {
      "t": 1632323700,
      "o": 145.155,
      "h": 145.23,
      "l": 145.03,
      "c": 145.115,
      "v": 16576
    },
    {
      "t": 1632324600,
      "o": 145.09,
      "h": 145.49,
      "l": 145.09,
      "c": 145.465,
      "v": 11880
    },
    {
      "t": 1632325500,
      "o": 145.48,
      "h": 145.88,
      "l": 145.36,
      "c": 145.54,
      "v": 25814
    },
    {
      "t": 1632326400,
      "o": 145.515,
      "h": 145.6,
      "l": 145.39,
      "c": 145.51,
      "v": 11709
    },
    {
      "t": 1632327300,
      "o": 145.5,
      "h": 145.605,
      "l": 145.45,
      "c": 145.5,
      "v": 15284
    },
    {
      "t": 1632328200,
      "o": 145.52,
      "h": 145.68,
      "l": 145.3,
      "c": 145.3,
      "v": 11692
    },
    {
      "t": 1632329100,
      "o": 145.31,
      "h": 145.35,
      "l": 145.205,
      "c": 145.23,
      "v": 5112
    },
    {
      "t": 1632330000,
      "o": 145.25,
      "h": 145.25,
      "l": 145.1,
      "c": 145.14,
      "v": 9865
    },
    {
      "t": 1632330900,
      "o": 145.13,
      "h": 145.31,
      "l": 145.06,
      "c": 145.285,
      "v": 14468
    },
    {
      "t": 1632331800,
      "o": 145.27,
      "h": 145.38,
      "l": 145.16,
      "c": 145.18,
      "v": 11980
    },
    {
      "t": 1632332700,
      "o": 145.17,
      "h": 145.285,
      "l": 145.045,
      "c": 145.285,
      "v": 18337
    },
    {
      "t": 1632333600,
      "o": 144.85,
      "h": 146.17,
      "l": 144.785,
      "c": 146.11,
      "v": 76212
    },
    {
      "t": 1632334500,
      "o": 146.24,
      "h": 146.41,
      "l": 145.83,
      "c": 146.12,
      "v": 43182
    },
    {
      "t": 1632335400,
      "o": 146.09,
      "h": 146.14,
      "l": 145.47,
      "c": 145.75,
      "v": 38008
    },
    {
      "t": 1632336300,
      "o": 145.65,
      "h": 145.7,
      "l": 145.07,
      "c": 145.34,
      "v": 41163
    },
    {
      "t": 1632337200,
      "o": 145.345,
      "h": 145.92,
      "l": 145.24,
      "c": 145.86,
      "v": 40966
    },
    {
      "t": 1632338100,
      "o": 145.83,
      "h": 146.43,
      "l": 145.79,
      "c": 146.175,
      "v": 50088
    },
    {
      "t": 1632339000,
      "o": 146.15,
      "h": 146.22,
      "l": 145.79,
      "c": 145.845,
      "v": 47495
    },
    {
      "t": 1632339900,
      "o": 145.85,
      "h": 146.24,
      "l": 145.7,
      "c": 145.82,
      "v": 188967
    },
    {
      "t": 1632341700,
      "o": 145.99,
      "h": 145.99,
      "l": 145.99,
      "c": 145.99,
      "v": 100
    },
    {
      "t": 1632403800,
      "o": 146.73,
      "h": 146.75,
      "l": 145.66,
      "c": 146.375,
      "v": 86329
    },
    {
      "t": 1632404700,
      "o": 146.37,
      "h": 146.63,
      "l": 146.2,
      "c": 146.25,
      "v": 32573
    },
    {
      "t": 1632405600,
      "o": 146.305,
      "h": 146.88,
      "l": 146.255,
      "c": 146.815,
      "v": 39334
    },
    {
      "t": 1632406500,
      "o": 146.88,
      "h": 146.97,
      "l": 146.67,
      "c": 146.8,
      "v": 23317
    },
    {
      "t": 1632407400,
      "o": 146.79,
      "h": 146.82,
      "l": 146.58,
      "c": 146.62,
      "v": 16768
    },
    {
      "t": 1632408300,
      "o": 146.685,
      "h": 147.08,
      "l": 146.665,
      "c": 146.96,
      "v": 34555
    },
    {
      "t": 1632409200,
      "o": 146.935,
      "h": 146.97,
      "l": 146.6,
      "c": 146.77,
      "v": 26179
    },
    {
      "t": 1632410100,
      "o": 146.805,
      "h": 146.84,
      "l": 146.37,
      "c": 146.66,
      "v": 21343
    },
    {
      "t": 1632411000,
      "o": 146.63,
      "h": 146.875,
      "l": 146.575,
      "c": 146.605,
      "v": 14025
    },
    {
      "t": 1632411900,
      "o": 146.65,
      "h": 146.94,
      "l": 146.55,
      "c": 146.895,
      "v": 10108
    },
    {
      "t": 1632412800,
      "o": 146.92,
      "h": 146.93,
      "l": 146.51,
      "c": 146.57,
      "v": 19320
    },
    {
      "t": 1632413700,
      "o": 146.605,
      "h": 146.7,
      "l": 146.53,
      "c": 146.53,
      "v": 15632
    },
    {
      "t": 1632414600,
      "o": 146.61,
      "h": 146.77,
      "l": 146.6,
      "c": 146.64,
      "v": 7844
    },
    {
      "t": 1632415500,
      "o": 146.67,
      "h": 146.78,
      "l": 146.59,
      "c": 146.6,
      "v": 8374
    },
    {
      "t": 1632416400,
      "o": 146.575,
      "h": 146.82,
      "l": 146.555,
      "c": 146.64,
      "v": 15771
    },
    {
      "t": 1632417300,
      "o": 146.64,
      "h": 146.915,
      "l": 146.64,
      "c": 146.915,
      "v": 8772
    },
    {
      "t": 1632418200,
      "o": 146.92,
      "h": 146.96,
      "l": 146.86,
      "c": 146.94,
      "v": 11788
    },
    {
      "t": 1632419100,
      "o": 146.97,
      "h": 147.02,
      "l": 146.9,
      "c": 147.02,
      "v": 9461
    },
    {
      "t": 1632420000,
      "o": 146.99,
      "h": 147,
      "l": 146.81,
      "c": 146.87,
      "v": 12087
    },
    {
      "t": 1632420900,
      "o": 146.87,
      "h": 146.89,
      "l": 146.72,
      "c": 146.76,
      "v": 9222
    },
    {
      "t": 1632421800,
      "o": 146.765,
      "h": 146.9,
      "l": 146.72,
      "c": 146.73,
      "v": 14209
    },
    {
      "t": 1632422700,
      "o": 146.79,
      "h": 146.98,
      "l": 146.73,
      "c": 146.95,
      "v": 9629
    },
    {
      "t": 1632423600,
      "o": 146.945,
      "h": 146.945,
      "l": 146.71,
      "c": 146.73,
      "v": 10290
    },
    {
      "t": 1632424500,
      "o": 146.74,
      "h": 146.905,
      "l": 146.71,
      "c": 146.8,
      "v": 33341
    },
    {
      "t": 1632425400,
      "o": 146.81,
      "h": 146.93,
      "l": 146.745,
      "c": 146.885,
      "v": 44562
    },
    {
      "t": 1632426300,
      "o": 146.895,
      "h": 146.97,
      "l": 146.725,
      "c": 146.9,
      "v": 177130
    },
    {
      "t": 1632427200,
      "o": 146.84,
      "h": 146.84,
      "l": 146.84,
      "c": 146.84,
      "v": 100
    },
    {
      "t": 1632429000,
      "o": 146.74,
      "h": 146.77,
      "l": 146.74,
      "c": 146.76,
      "v": 400
    },
    {
      "t": 1632489300,
      "o": 145.79,
      "h": 145.79,
      "l": 145.79,
      "c": 145.79,
      "v": 100
    },
    {
      "t": 1632490200,
      "o": 145.63,
      "h": 146.28,
      "l": 145.57,
      "c": 146.13,
      "v": 52202
    },
    {
      "t": 1632491100,
      "o": 146.16,
      "h": 146.21,
      "l": 145.79,
      "c": 146.17,
      "v": 32852
    },
    {
      "t": 1632492000,
      "o": 146.2,
      "h": 146.385,
      "l": 145.84,
      "c": 145.91,
      "v": 24228
    },
    {
      "t": 1632492900,
      "o": 145.845,
      "h": 146.23,
      "l": 145.765,
      "c": 146.11,
      "v": 28608
    },
    {
      "t": 1632493800,
      "o": 146.16,
      "h": 146.32,
      "l": 146,
      "c": 146.085,
      "v": 28661
    },
    {
      "t": 1632494700,
      "o": 146.075,
      "h": 146.075,
      "l": 145.75,
      "c": 145.865,
      "v": 26071
    },
    {
      "t": 1632495600,
      "o": 145.91,
      "h": 146.195,
      "l": 145.9,
      "c": 146.065,
      "v": 17681
    },
    {
      "t": 1632496500,
      "o": 146.05,
      "h": 146.155,
      "l": 145.87,
      "c": 145.93,
      "v": 18054
    },
    {
      "t": 1632497400,
      "o": 145.94,
      "h": 146.09,
      "l": 145.88,
      "c": 146.035,
      "v": 14910
    },
    {
      "t": 1632498300,
      "o": 146.05,
      "h": 146.425,
      "l": 146.02,
      "c": 146.425,
      "v": 13655
    },
    {
      "t": 1632499200,
      "o": 146.425,
      "h": 146.63,
      "l": 146.295,
      "c": 146.57,
      "v": 13142
    },
    {
      "t": 1632500100,
      "o": 146.575,
      "h": 146.64,
      "l": 146.49,
      "c": 146.6,
      "v": 14658
    },
    {
      "t": 1632501000,
      "o": 146.58,
      "h": 146.68,
      "l": 146.445,
      "c": 146.68,
      "v": 16417
    },
    {
      "t": 1632501900,
      "o": 146.715,
      "h": 146.78,
      "l": 146.55,
      "c": 146.61,
      "v": 7477
    },
    {
      "t": 1632502800,
      "o": 146.6,
      "h": 146.7,
      "l": 146.5,
      "c": 146.63,
      "v": 7704
    },
    {
      "t": 1632503700,
      "o": 146.625,
      "h": 146.77,
      "l": 146.61,
      "c": 146.74,
      "v": 8458
    },
    {
      "t": 1632504600,
      "o": 146.785,
      "h": 146.88,
      "l": 146.61,
      "c": 146.88,
      "v": 16298
    },
    {
      "t": 1632505500,
      "o": 146.88,
      "h": 146.89,
      "l": 146.705,
      "c": 146.71,
      "v": 12808
    },
    {
      "t": 1632506400,
      "o": 146.77,
      "h": 146.83,
      "l": 146.705,
      "c": 146.71,
      "v": 11278
    },
    {
      "t": 1632507300,
      "o": 146.72,
      "h": 146.87,
      "l": 146.72,
      "c": 146.85,
      "v": 11548
    },
    {
      "t": 1632508200,
      "o": 146.83,
      "h": 146.895,
      "l": 146.78,
      "c": 146.84,
      "v": 11803
    },
    {
      "t": 1632509100,
      "o": 146.85,
      "h": 147.03,
      "l": 146.85,
      "c": 147.02,
      "v": 16897
    },
    {
      "t": 1632510000,
      "o": 147,
      "h": 147,
      "l": 146.825,
      "c": 146.92,
      "v": 11803
    },
    {
      "t": 1632510900,
      "o": 146.97,
      "h": 147.28,
      "l": 146.96,
      "c": 147.265,
      "v": 31925
    },
    {
      "t": 1632511800,
      "o": 147.27,
      "h": 147.465,
      "l": 147.2,
      "c": 147.3,
      "v": 42970
    },
    {
      "t": 1632512700,
      "o": 147.285,
      "h": 147.35,
      "l": 146.83,
      "c": 147.03,
      "v": 127585
    }
  ]
}

var newsdata = {
  "data": [
    {
      "news_url": "https://www.cnbc.com/2021/09/24/apple-told-a-showbiz-union-it-had-less-than-20-million-tv-subscribers.html",
      "image_url": "https://cdn.snapi.dev/images/v1/a/w/106122287-1568139145268gettyimages-1167242360-1022499.jpeg",
      "title": "Apple claimed it had less than 20 million TV+ subscribers in July, showbiz union says",
      "text": "This allowed Apple to pay behind-the-scenes production crew lower rates than other streamers, the International Alliance of Theatrical Stage Employees says.",
      "source_name": "CNBC",
      "date": "Fri, 24 Sep 2021 21:14:39 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.youtube.com/watch?v=okxZhnzYR_0",
      "image_url": "https://cdn.snapi.dev/images/v1/m/q/iphone-13-surpasses-expectations-1022330.jpg",
      "title": "iPhone 13 surpasses expectations",
      "text": "Joanna Stern, personal tech columnist at WSJ, and Amit Daryanani, fundamental research analyst at Evercore ISI, join The Exchange to discuss the launch of Apple's new iPhone and whether the specs live up to what was promised.",
      "source_name": "CNBC Television",
      "date": "Fri, 24 Sep 2021 16:41:39 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Video",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.youtube.com/watch?v=zcw2MixTM-Q",
      "image_url": "https://cdn.snapi.dev/images/v1/m/q/apple-doing-good-job-on-supply-chain-as-new-iphone-debuts-ray-wang-1022285.jpg",
      "title": "Apple doing ‘good job' on supply chain as new iPhone debuts: Ray Wang",
      "text": "Constellation Research CEO Ray Wang provides insight into supply chain issues in the tech industry and the latest iPhone. #FOXBusiness Subscribe to Fox Business!",
      "source_name": "Fox Business",
      "date": "Fri, 24 Sep 2021 16:00:12 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Positive",
      "type": "Video",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://nypost.com/2021/09/24/apple-releases-new-iphone-13-line-as-fans-flock-to-stores/",
      "image_url": "https://cdn.snapi.dev/images/v1/3/1/apple-ny-compojpgquality90stripall-1022190.jpg",
      "title": "Apple releases new iPhones today as fans flock to stores",
      "text": "Apple's new line of iPhones hit shelves Friday, bringing crowds back to stores after last year's launch event saw muted crowds amid the pandemic.",
      "source_name": "New York Post",
      "date": "Fri, 24 Sep 2021 14:53:49 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://investorplace.com/2021/09/millennials-are-pioneering-the-next-megatrend-heres-how/",
      "image_url": "https://cdn.snapi.dev/images/v1/r/v/computer-electronic25-1021865.jpg",
      "title": "Millennials Are Pioneering the Next Megatrend: Here's How",
      "text": "Americans spend more time looking at their phones now than they did looking at any screen in 2008. Screen time is the new megatrend to watch.",
      "source_name": "InvestorPlace",
      "date": "Fri, 24 Sep 2021 11:34:33 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL",
        "AMZN",
        "FB",
        "GOOG",
        "GOOGL",
        "MELI",
        "NVDA",
        "SQ"
      ]
    },
    {
      "news_url": "https://www.cnbc.com/2021/09/24/apples-ios-changes-hurt-facebooks-ad-business.html",
      "image_url": "https://cdn.snapi.dev/images/v1/j/s/106941688-1631644205737-appleeventsep14keynotetim-cook02-1021817.jpg",
      "title": "Apple's power move to kneecap Facebook advertising is working",
      "text": "Apple's anti-tracking initiative is harming Facebook's ad effectiveness and its bottom line, CNBC contributor Alex Kantrowitz says in an op-ed.",
      "source_name": "CNBC",
      "date": "Fri, 24 Sep 2021 11:20:01 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "FB"
      ]
    },
    {
      "news_url": "https://invezz.com/news/2021/09/24/dan-ives-apple-could-hit-a-3-trillion-market-cap-in-2022/",
      "image_url": "https://cdn.snapi.dev/images/v1/i/k/apple-potential-1021639.jpg",
      "title": "Dan Ives: ‘Apple could hit a $3 trillion market cap in 2022'",
      "text": "Apple Inc (NASDAQ: AAPL) launched the iPhone 13 last week that Wedbush Securities' Dan Ives says is what's working for Apple right now. The flagship smartphone will be available at stores from today.",
      "source_name": "Invezz",
      "date": "Fri, 24 Sep 2021 09:57:42 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.cnbc.com/2021/09/24/op-ed-apples-ios-changes-hurt-facebooks-ad-business.html",
      "image_url": "https://cdn.snapi.dev/images/v1/2/s/106932970-1629895691546-gettyimages-1233496372-skorea-sk-telecom-1021504.jpeg",
      "title": "Op-ed: Apple's power move to kneecap Facebook advertising is working",
      "text": "Apple's anti-tracking initiative is harming Facebook's ad effectiveness and its bottom line, CNBC contributor Alex Kantrowitz says in an op-ed.",
      "source_name": "CNBC",
      "date": "Fri, 24 Sep 2021 08:56:49 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL",
        "FB"
      ]
    },
    {
      "news_url": "https://www.youtube.com/watch?v=y8Iba3OL6hA",
      "image_url": "https://cdn.snapi.dev/images/v1/l/p/wall-street-is-underestimating-apple-upgrade-cycle-dan-ives-1021499.jpg",
      "title": "Wall Street is underestimating Apple upgrade cycle: Dan Ives",
      "text": "CNBC's \"Squawk Box\" team discusses Apple's iPhones and Big Tech stocks with Dan Ives of Wedbush Securities.",
      "source_name": "CNBC Television",
      "date": "Fri, 24 Sep 2021 08:41:28 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Video",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://247wallst.com/technology-3/2021/09/24/whats-up-with-apple-europe-oks-usb-standard-star-dreck-and-more/",
      "image_url": "https://cdn.snapi.dev/images/v1/1/0/105852533-1555339384013gettyimages-1089723090600x337-1021621.jpeg",
      "title": "What's Up With Apple: Europe OKs USB Standard, ‘Star Dreck' and More",
      "text": "As expected, the European Commission (EC) on Thursday released a proposal that would require manufacturers of electronic devices to use a common design for their device chargers.",
      "source_name": "24/7 Wall Street",
      "date": "Fri, 24 Sep 2021 07:49:08 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.benzinga.com/news/21/09/23085387/apple-warns-restoring-from-backup-on-iphone-13-new-ipad-model-may-lead-to-some-bugs",
      "image_url": "https://cdn.snapi.dev/images/v1/a/p/apple-iphone-13-pro-new-camera-system-09142021-3-1021367.jpg",
      "title": "Apple Warns Restoring From Backup On iPhone 13, New iPad Model May Lead To Some Bugs",
      "text": "Apple Inc. (NASDAQ: AAPL) has warned that restoring from backup might result in bugs related to Apple Music and iOS 15 widgets on the new iPhone 13 and iPad models. What Happened: Apple said in a new support document published Thursday that customers who try to restore their new iPhone or iPad devices from a backup may not be able to access the Apple Music catalog, Apple Music settings, or use the app's Sync Library.",
      "source_name": "Benzinga",
      "date": "Fri, 24 Sep 2021 07:09:25 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.benzinga.com/news/21/09/23084594/apple-google-sued-by-jawbone-over-patent-violations-in-noise-canceling-technology-report",
      "image_url": "https://cdn.snapi.dev/images/v1/6/v/m02d20210615t2i1565741131w940fhfwllplsqrlynxnpeh5e0hv-868224-1021225.jpg",
      "title": "Apple, Google Sued By Jawbone Over Patent Violations In Noise-Canceling Technology: Report",
      "text": "Apple Inc (NASDAQ: AAPL) and Alphabet Inc (NASDAQ: GOOGL) (NASDAQ: GOOG) have been sued by bankrupt wearable technology company Jawbones Inc, Bloomberg News reported on Thursday, citing court documents.  What Happened: Jawbone, which went bankrupt in 2017, has sued the two tech giants over the noise cancellation technology in their earbuds, smartphones and smart home devices, as per Bloomberg.",
      "source_name": "Benzinga",
      "date": "Fri, 24 Sep 2021 05:39:16 -0400",
      "topics": [],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "GOOG",
        "GOOGL",
        "AAPL"
      ]
    },
    {
      "news_url": "https://seekingalpha.com/article/4456862-whats-next-for-apple-and-tim-cook",
      "image_url": "https://cdn.snapi.dev/images/v1/a/c/apple-1021139.jpg",
      "title": "What's Next For Apple And Tim Cook",
      "text": "Tim Cook has created more absolute value than any other CEO. Under his reign Apple's market cap went from $350 billion to $2.4 trillion.",
      "source_name": "Seeking Alpha",
      "date": "Fri, 24 Sep 2021 02:06:13 -0400",
      "topics": [
        "CEO",
        "paylimitwall"
      ],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.zacks.com/commentary/1800328/consumer-prices-the-fed-and-tapering",
      "image_url": "https://cdn.snapi.dev/images/v1/p/s/ree3-1-822951-827757-1021003.jpg",
      "title": "Consumer Prices, the Fed, and Tapering",
      "text": "Can we make sense of this trio?",
      "source_name": "Zacks Investment Research",
      "date": "Thu, 23 Sep 2021 19:05:16 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL",
        "CICHY",
        "SHOP"
      ]
    },
    {
      "news_url": "https://www.zacks.com/stock/news/1800315/market-outlook-etf-ideas-for-the-fourth-quarter",
      "image_url": "https://cdn.snapi.dev/images/v1/9/h/apple-software-patch-cybersecurity-457x274-1011172-1020888.jpg",
      "title": "Market Outlook & ETF Ideas for the Fourth Quarter",
      "text": "We discuss why it makes sense to look at quality and small cap stocks now.",
      "source_name": "Zacks Investment Research",
      "date": "Thu, 23 Sep 2021 17:34:03 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "CVX",
        "GME",
        "M",
        "MSFT",
        "SDY",
        "SPSM",
        "T",
        "XOM"
      ]
    },
    {
      "news_url": "https://www.gurufocus.com/news/1530387/quality-growth-stocks-that-gurus-love",
      "image_url": "https://cdn.snapi.dev/images/v1/g/f/software40-2-1020885.jpg",
      "title": "Quality Growth Stocks That Gurus Love",
      "text": "It's rare for a stock to be able to achieve average annual returns above 20% for an entire decade. It's even more rare to do so while the company is consistently growing its top and bottom lines and maintaining a strong balance sheet.",
      "source_name": "GuruFocus",
      "date": "Thu, 23 Sep 2021 17:31:23 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "GOOGL",
        "HD",
        "LOW",
        "MSFT",
        "TMO",
        "TSM",
        "GOOG"
      ]
    },
    {
      "news_url": "https://www.gurufocus.com/news/1530233/european-union-puts-apple-in-the-spotlight-with-common-charger-directive",
      "image_url": "https://cdn.snapi.dev/images/v1/z/9/urlhttps3a2f2fgfoolcdncom2feditorial2fimages2f5176902fairpods-1020649.jpg",
      "title": "European Union Puts Apple in the Spotlight With Common Charger Directive",
      "text": "The European Commission, the executive branch of the European Union, has proposed a new law that seeks to standardize charging ports across smartphone manufacturers and other electronics.",
      "source_name": "GuruFocus",
      "date": "Thu, 23 Sep 2021 15:53:48 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://techxplore.com/news/2021-09-fortnite-blacklisted-app-legal-apple.html",
      "image_url": "https://cdn.snapi.dev/images/v1/w/g/app-store-1020348.jpg",
      "title": "Fortnite to be 'blacklisted' from App Store until legal battle with Apple subsides, Epic Games CEO says",
      "text": "The CEO and founder of Epic Games says Fortnite will continue to be \"blacklisted\" from Apple's App Store, the latest in a push-and-shove between the gaming company and the tech giant.",
      "source_name": "TechXplore",
      "date": "Thu, 23 Sep 2021 12:42:35 -0400",
      "topics": [
        "CEO"
      ],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.benzinga.com/government/21/09/23071489/eus-latest-proposal-could-mean-setback-for-apple-report",
      "image_url": "https://cdn.snapi.dev/images/v1/c/o/computer-electronic6-1020405.jpg",
      "title": "EU's Latest Proposal Could Mean Setback For Apple: Report",
      "text": "The EU's proposal for a standard charging port for mobile phones, tablets, and headphones could prove to be a massive setback for Apple Inc (NASDAQ: AAPL) versus its peers, Reuters reports. The EU proposed a USB-C connector as the standard port for all smartphones, tablets, cameras, headphones, portable speakers and handheld video games consoles.",
      "source_name": "Benzinga",
      "date": "Thu, 23 Sep 2021 12:22:11 -0400",
      "topics": [],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.zacks.com/stock/news/1800232/apple-aapl-rumored-to-launch-ar-supported-headset-in-2h22",
      "image_url": "https://cdn.snapi.dev/images/v1/y/r/urlhttps3a2f2fgfoolcdncom2feditorial2fimages2f5166132fnew-ipad-air-and-ipad-mini-with-apple-pencil-03182019-1020296.jpg",
      "title": "Apple (AAPL) Rumored to Launch AR-Supported Headset in 2H22",
      "text": "Apple (AAPL) can launch its first AR headset in the second half of 2022, per a Digitimes report.",
      "source_name": "Zacks Investment Research",
      "date": "Thu, 23 Sep 2021 12:14:05 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.cnn.com/2021/09/23/tech/europe-phone-chargers-apple/index.html",
      "image_url": "https://cdn.snapi.dev/images/v1/o/9/210923091605-eu-phone-chargers-super-169-1020123.jpg",
      "title": "Europe will require USB-C chargers. Apple isn't happy",
      "text": "The European Union proposed new rules on Thursday that will make USB-C ports standard on smartphones, tablets, cameras, phones, portable speakers and handheld video game systems.",
      "source_name": "CNN Business",
      "date": "Thu, 23 Sep 2021 10:59:43 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.businessinsider.com/apple-may-be-forced-iphone-charging-ports-usb-c-europe-2021-9",
      "image_url": "https://cdn.snapi.dev/images/v1/6/1/6143b59b2db0850019a9e7c9formatjpeg-1020125.jpg",
      "title": "Apple may be forced to change the iPhone's proprietary charging port under new EU proposal",
      "text": "The new mandate would require all electronics makers to adopt USB-C ports - including Apple's iPhone, which uses a proprietary \"Lightning\" port.",
      "source_name": "Business Insider",
      "date": "Thu, 23 Sep 2021 10:51:06 -0400",
      "topics": [
        "product",
        "paylimitwall"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.benzinga.com/analyst-ratings/analyst-color/21/09/23072338/apple-analyst-iphone-13-preorders-look-robust-out-of-the-gate",
      "image_url": "https://cdn.snapi.dev/images/v1/a/p/apple-iphone-13-pro-new-camera-system-09142021-2-1020124.jpg",
      "title": "Apple Analyst: 'iPhone 13 Preorders Look Robust Out Of The Gate'",
      "text": "Apple, Inc. (NASDAQ:AAPL) launched the iPhone 13 models on Sept. 14, and early indications regarding the uptake have been grossly positive.",
      "source_name": "Benzinga",
      "date": "Thu, 23 Sep 2021 10:40:53 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://247wallst.com/investing/2021/09/23/buy-the-dip-traders-pounced-on-these-10-red-hot-stocks-after-the-massive-monday-sell-off/",
      "image_url": "https://cdn.snapi.dev/images/v1/a/l/the-3-worst-looking-dow-stocks-from-a-price-chart-perspective-1003611-1020032.jpg",
      "title": "Buy-the-Dip Traders Pounced on These 10 Red-Hot Stocks After the Massive Monday Sell-Off",
      "text": "While the damage, at least for the time being, appears to be rather short-lived, Monday's huge sell-off, when combined with the previous two weeks of selling, finally pushed the market down 5%.",
      "source_name": "24/7 Wall Street",
      "date": "Thu, 23 Sep 2021 10:37:43 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "AMC",
        "AMD",
        "BABA",
        "INTC",
        "LVS",
        "MSFT",
        "NVDA",
        "VZ",
        "WYNN"
      ]
    },
    {
      "news_url": "https://www.barrons.com/articles/iphone-13-preorders-china-demand-51632407223",
      "image_url": "https://cdn.snapi.dev/images/v1/a/p/apple-1-457x274-1007869-1020121.jpg",
      "title": "iPhone 13 Preorders Soar. This Analyst Says Consumers Want Apple's Newest Model.",
      "text": "Demand for Apple's newest iPhone lineup is being led by consumers in China.",
      "source_name": "Barrons",
      "date": "Thu, 23 Sep 2021 10:36:00 -0400",
      "topics": [
        "product",
        "paywall"
      ],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://nypost.com/2021/09/23/apple-would-have-to-redesign-iphone-under-proposed-eu-rule/",
      "image_url": "https://cdn.snapi.dev/images/v1/o/f/apple-chargers-02jpgquality90stripall-1020020.jpg",
      "title": "Apple would have to redesign iPhone under proposed EU charger rule",
      "text": "Apple's current slate of iPhones would be illegal to sell in the European Union under a new proposed rule requiring smartphones and electronics to come with standardized charging ports.",
      "source_name": "New York Post",
      "date": "Thu, 23 Sep 2021 10:19:25 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://investorplace.com/2021/09/3-september-selloff-stocks-to-buy/",
      "image_url": "https://cdn.snapi.dev/images/v1/u/2/the-3-worst-looking-dow-stocks-from-a-price-chart-perspective-1003611-1019957.jpg",
      "title": "3 September Selloff Stocks to Buy",
      "text": "September has conspired against overzealous bulls, but today there are stocks to buy with improved odds for investing success. The post 3 September Selloff Stocks to Buy appeared first on InvestorPlace.",
      "source_name": "InvestorPlace",
      "date": "Thu, 23 Sep 2021 09:46:56 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "DAL",
        "TDOC"
      ]
    },
    {
      "news_url": "https://www.cnet.com/tech/mobile/eu-may-force-apple-to-add-usb-c-ports-to-iphones/",
      "image_url": "https://cdn.snapi.dev/images/v1/2/0/20190326-usb-c-004-1020096.jpg",
      "title": "EU may force Apple to add USB-C ports to iPhones",
      "text": "A proposal would require a universal charging solution for phones and other devices.",
      "source_name": "CNET",
      "date": "Thu, 23 Sep 2021 09:28:29 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.fool.com/investing/2021/09/23/september-sell-off-2-top-stocks-to-buy-now/",
      "image_url": "https://cdn.snapi.dev/images/v1/u/r/urlhttps3a2f2fgfoolcdncom2feditorial2fimages2f6440282fwoman-buying-stock-mobile-phonejpgw700opresize-1020092.jpg",
      "title": "September Sell-Off: 2 Top Stocks to Buy Now",
      "text": "These tech stocks could be great buys for the long run amid a potential stock market correction.",
      "source_name": "The Motley Fool",
      "date": "Thu, 23 Sep 2021 09:24:15 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "NVDA"
      ]
    },
    {
      "news_url": "https://www.theguardian.com/world/2021/sep/23/apple-opposes-eu-plans-to-make-common-charger-port-for-all-devices",
      "image_url": "https://cdn.snapi.dev/images/v1/3/4/3444jpgwidth460quality85autoformatfitmaxs208d64463fa18d5d7fdfe0b783aaad0b-1020089.jpg",
      "title": "Apple opposes EU plans to make common charger port for all devices",
      "text": "Proposals to make USB-C port mandatory could reduce electronic waste and save EU consumers money",
      "source_name": "The Guardian",
      "date": "Thu, 23 Sep 2021 09:19:11 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://techncruncher.blogspot.com/2021/09/in-setback-for-apple-eu-plans-one.html",
      "image_url": "https://cdn.snapi.dev/images/v1/1/0/106890411-1622469214911-gettyimages-1233110710-bc-apple-7737-985708-1019865.jpeg",
      "title": "In setback for Apple, EU plans one mobile charging port for all",
      "text": "The EU aims to have a common charging port for mobile phones, tablets and headphones under a European Commission proposal presented on Thursday in a world first, with the move impacting iPhone maker Apple more than its rivals. The move has been more than 10 years in the making, with the European Union executive touting environmental benefits and 250 million euros ($293 million) in annual savings for users.",
      "source_name": "TechCrunch",
      "date": "Thu, 23 Sep 2021 09:14:00 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.cnbc.com/2021/09/23/eu-plans-to-make-usb-c-mandatory-for-apple-iphones-and-other-devices.html",
      "image_url": "https://cdn.snapi.dev/images/v1/u/c/106946310-1632402631286-gettyimages-1336734432-dscf4797-1019858.jpeg",
      "title": "EU plans to make common charger mandatory for Apple iPhones and other devices",
      "text": "The EU has put forward a new law that would force smartphone manufacturers and other electronics makers to equip their devices with a USB-C charging port.",
      "source_name": "CNBC",
      "date": "Thu, 23 Sep 2021 09:12:26 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://seekingalpha.com/article/4456666-apple-a-clash-on-wall-street",
      "image_url": "https://cdn.snapi.dev/images/v1/r/0/app9-6-1019748.jpg",
      "title": "Apple: A Clash On Wall Street",
      "text": "Actively managed funds have been spurning Apple such that it is the most underweighted stock by active global fund managers. Apple sustains its growth with a combination of perfecting and adapting old products while wrapping them into its ever-expanding ecosystem.",
      "source_name": "Seeking Alpha",
      "date": "Thu, 23 Sep 2021 09:00:00 -0400",
      "topics": [
        "paylimitwall"
      ],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.zacks.com/stock/news/1799919/fed-gives-bond-buy-tapering-signal-without-timeline-5-picks",
      "image_url": "https://cdn.snapi.dev/images/v1/h/g/computer-electronic35-1019635.jpg",
      "title": "Fed Gives Bond-Buy Tapering Signal Without Timeline: 5 Picks",
      "text": "We have narrowed down our search to five U.S. corporate behemoths that have strong growth potential for the rest of 2021. These are: AAPL, MSFT, NVDA, DHR and COST.",
      "source_name": "Zacks Investment Research",
      "date": "Thu, 23 Sep 2021 08:25:09 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "COST",
        "DHR",
        "MSFT",
        "NVDA"
      ]
    },
    {
      "news_url": "https://www.zacks.com/stock/news/1799916/5-stocks-to-watch-amid-surging-demand-for-digital-payments",
      "image_url": "https://cdn.snapi.dev/images/v1/t/u/sim123-994892-1019636.jpg",
      "title": "5 Stocks to Watch Amid Surging Demand for Digital Payments",
      "text": "Watch out for stocks like Apple (AAPL), Usio (USIO), EVERTEC (EVTC), Visa (V) and Alphabet (GOOGL) amid continued demand for digital payment as a safe and convenient way of transacting.",
      "source_name": "Zacks Investment Research",
      "date": "Thu, 23 Sep 2021 08:25:09 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL",
        "EVTC",
        "GOOGL",
        "USIO",
        "V"
      ]
    },
    {
      "news_url": "https://www.reuters.com/article/usa-white-house-tech-meeting-exclusive/exclusive-u-s-and-eu-look-to-work-more-closely-in-regulating-big-tech-at-summit-idUSKBN2GJ0YX",
      "image_url": "https://cdn.snapi.dev/images/v1/a/m/amazon-and-google-face-uk-investigation-into-fake-reviews-882999-1019641.jpg",
      "title": "Exclusive-U.S. and EU look to work more closely in regulating Big Tech at summit",
      "text": "The United States and European Union plan to take a more unified approach to limit the growing market power of Big Tech companies, according to a draft memo seen by Reuters.",
      "source_name": "Reuters",
      "date": "Thu, 23 Sep 2021 08:04:00 -0400",
      "topics": [],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL",
        "AMZN",
        "FB",
        "GOOG",
        "GOOGL"
      ]
    },
    {
      "news_url": "https://www.nytimes.com/2021/09/23/business/european-union-apple-charging-port.html",
      "image_url": "https://cdn.snapi.dev/images/v1/m/e/merlin-195078531-1dd74f19-167a-4ccc-bfa8-9332018be47d-articlelargejpgquality75autowebpdisableupscale-1019496.jpg",
      "title": "E.U. Seeks Common Charger for All Phones, Hurting Apple",
      "text": "The push to make USB-C connectors standard on all mobile devices would primarily affect Apple, which uses proprietary technology for charging.",
      "source_name": "NYTimes",
      "date": "Thu, 23 Sep 2021 07:30:07 -0400",
      "topics": [
        "product",
        "paywall"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.reuters.com/business/media-telecom/eu-plans-one-mobile-charging-port-all-setback-apple-2021-09-23/",
      "image_url": "https://cdn.snapi.dev/images/v1/p/t/m02d20210923t2i1575699864w940fhfwllplsqrlynxmpeh8m0el-1019453.jpg",
      "title": "EU plans one mobile charging port for all, in setback for Apple",
      "text": "The European Union aims to have a common charging port for mobile phones, tablets and headphones under a European Commission proposal presented on Thursday in a world first, with the move impacting iPhone maker Apple more than its rivals.",
      "source_name": "Reuters",
      "date": "Thu, 23 Sep 2021 07:25:00 -0400",
      "topics": [
        "product"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.fool.com/investing/2021/09/23/better-faang-stock-facebook-vs-apple/",
      "image_url": "https://cdn.snapi.dev/images/v1/t/j/urlhttps3a2f2fgfoolcdncom2feditorial2fimages2f6371902fgettyimages-1223455328jpgw700opresize-953108-995058-1019432.jpg",
      "title": "Better FAANG Stock: Facebook vs. Apple",
      "text": "Which tech giant is the better overall investment?",
      "source_name": "The Motley Fool",
      "date": "Thu, 23 Sep 2021 07:15:00 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL",
        "FB"
      ]
    },
    {
      "news_url": "https://www.fool.com/investing/2021/09/23/2-faang-stocks-to-buy-1-to-avoid-in-fourth-quarter/",
      "image_url": "https://cdn.snapi.dev/images/v1/u/r/urlhttps3a2f2fgfoolcdncom2feditorial2fimages2f5268552fgettyimages-913219882jpgw700opresize-855175-875365-1019333.jpg",
      "title": "2 FAANG Stocks to Buy and 1 to Avoid in the Fourth Quarter",
      "text": "Among Facebook, Apple, Amazon, Netflix, and Alphabet (formerly Google), there are two clear-cut buys and one popular stock to steer clear of.",
      "source_name": "The Motley Fool",
      "date": "Thu, 23 Sep 2021 06:06:00 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL",
        "AMZN",
        "FB"
      ]
    },
    {
      "news_url": "https://www.reuters.com/technology/australias-commonwealth-bank-mocks-apples-pro-competition-claim-2021-09-23/",
      "image_url": "https://cdn.snapi.dev/images/v1/4/4/m02d20210923t2i1575667322w940fhfwllplsqrlynxmpeh8m033-1019163.jpg",
      "title": "Australia's Commonwealth Bank mocks Apple's 'pro-competition' claim",
      "text": "Commonwealth Bank of Australia, the country's largest lender, accused Apple Inc on Thursday of uncompetitive behaviour over control of payments on its phones, which have grown to about a third of all consumer payments.",
      "source_name": "Reuters",
      "date": "Thu, 23 Sep 2021 00:33:00 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.marketwatch.com/story/white-house-to-host-intel-apple-microsoft-execs-to-discuss-chip-shortage-11632364882",
      "image_url": "https://cdn.snapi.dev/images/v1/3/1/social-1019137.jpg",
      "title": "White House to host Intel, Apple, Microsoft execs to discuss chip shortage",
      "text": "The CEO of Intel Corp. INTC, +1.19% will join executives from Apple Inc. AAPL, +1.69% , Microsoft Corp. MSFT, +1.28% , Ford Motor Co. F, +3.60% and others at a White House virtual meeting Thursday to address the global chip shortage, Reuters reported Wednesday.",
      "source_name": "Market Watch",
      "date": "Wed, 22 Sep 2021 22:41:00 -0400",
      "topics": [
        "Political",
        "paylimitwall"
      ],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL",
        "INTC",
        "MSFT"
      ]
    },
    {
      "news_url": "https://www.fool.com/investing/2021/09/22/facebook-blames-apple-for-its-ad-business-woes-ami/",
      "image_url": "https://cdn.snapi.dev/images/v1/2/0/2025ofkoh-1-1019084.jpg",
      "title": "Facebook Blames Apple for Its Ad Business Woes Amid Escalating Feud",
      "text": "Is it ethical to take someone's personal data, then shove targeted ads in front of them that earn you billions of dollars, all in exchange for.",
      "source_name": "The Motley Fool",
      "date": "Wed, 22 Sep 2021 20:00:30 -0400",
      "topics": [],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL",
        "FB"
      ]
    },
    {
      "news_url": "https://venturebeat.com/2021/09/22/apple-tells-epic-that-fortnite-isnt-welcome-back-on-ios-yet/",
      "image_url": "https://cdn.snapi.dev/images/v1/f/o/fortnite-oneplus-8jpgw930-1019081.jpg",
      "title": "Apple tells Epic that Fortnite isn't welcome back on iOS yet",
      "text": "Fortnite is not returning to iPhone any time soon, according to a statement that Apple sent to Epic. It turns out Apple doesn't like Epic.",
      "source_name": "VentureBeat",
      "date": "Wed, 22 Sep 2021 19:53:34 -0400",
      "topics": [],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.cnn.com/2021/09/22/tech/apple-fortnite-epic-app-store/index.html",
      "image_url": "https://cdn.snapi.dev/images/v1/p/m/210922142313-fortnite-iphone-file-super-169-1018981.jpg",
      "title": "Apple denies Fortnite a return to the App Store",
      "text": "Apple will not allow Fortnite back on its devices until its legal battle with the video game's maker, Epic Games, has fully concluded, potentially delaying the game's return to iPhones by several years.",
      "source_name": "CNN Business",
      "date": "Wed, 22 Sep 2021 18:10:46 -0400",
      "topics": [],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.reuters.com/article/apple-bonuses/apple-to-pay-bonuses-of-up-to-1000-to-store-employees-bloomberg-news-idUSKBN2GI25J",
      "image_url": "https://cdn.snapi.dev/images/v1/x/q/m02d20210922t2i1575639020w940fhfwllplsqrlynxmpeh8l14s-1018939.jpg",
      "title": "Apple to pay bonuses of up to $1,000 to store employees",
      "text": "Apple Inc will pay one-time bonuses of as much as $1,000 to store employees next month, Bloomberg News reported on Wednesday, citing people familiar with the matter.",
      "source_name": "Reuters",
      "date": "Wed, 22 Sep 2021 17:28:00 -0400",
      "topics": [],
      "sentiment": "Positive",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.pymnts.com/healthcare/2021/apple-takes-bigger-bite-into-healthcare-tracking-renewing-debate-on-data-privacy/",
      "image_url": "https://cdn.snapi.dev/images/v1/a/p/apple-health-data-457x274-1018929.jpg",
      "title": "Apple Takes Bigger Bite Into Healthcare Tracking, Renewing Debate on Data Privacy",
      "text": "In the latest development in the push to use smartphones and wearables to track users' health, Apple Inc. is looking into ways to use its devices to detect and diagnose mental health conditions. It's part of a series of similar undertakings with implications for consumers' most sensitive personal information — their health data — and […]",
      "source_name": "PYMNTS",
      "date": "Wed, 22 Sep 2021 17:23:28 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.theguardian.com/technology/2021/sep/22/fortnite-app-store-download-banned-apple-legal-battle",
      "image_url": "https://cdn.snapi.dev/images/v1/4/6/4673jpgwidth460quality85autoformatfitmaxsc8c6d3a48e02386229c7af799533ca74-1018909.jpg",
      "title": "Apple bans Fortnite from App Store indefinitely as legal battle continues",
      "text": "Epic CEO condemns ‘extraordinary anticompetitive move by Apple' in case that could take years",
      "source_name": "The Guardian",
      "date": "Wed, 22 Sep 2021 16:50:44 -0400",
      "topics": [],
      "sentiment": "Neutral",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://www.businessinsider.com/fortnite-may-never-return-to-the-iphone-2021-9",
      "image_url": "https://cdn.snapi.dev/images/v1/5/c/5cace2d6d2ce786fce0cd46fformatjpeg-1018853.jpg",
      "title": "'Fortnite' may not return to iPhones for up to 5 years, if ever",
      "text": "Sorry \"Fortnite\" fans: It's starting to sound like the world's biggest game may never return to Apple's iPhones.",
      "source_name": "Business Insider",
      "date": "Wed, 22 Sep 2021 16:37:08 -0400",
      "topics": [
        "product",
        "paylimitwall"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL"
      ]
    },
    {
      "news_url": "https://markets.businessinsider.com/news/stocks/facebook-stock-advertising-ads-performance-privacy-apple-ios-tracking-devices-2021-9",
      "image_url": "https://cdn.snapi.dev/images/v1/5/f/5f5b7224e6ff30001d4e8400formatjpeg-1018696.jpg",
      "title": "Facebook falls 5% after it says Apple privacy update led to negative performance for advertisers on the platform",
      "text": "Facebook shares tumbled on Wednesday as the company estimated it has underreported iOS web conversions by roughly 15%.",
      "source_name": "Business Insider",
      "date": "Wed, 22 Sep 2021 15:40:24 -0400",
      "topics": [
        "paylimitwall"
      ],
      "sentiment": "Negative",
      "type": "Article",
      "tickers": [
        "AAPL",
        "FB"
      ]
    }
  ]
}


