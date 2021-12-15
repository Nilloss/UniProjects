//Sentiment libaries - 1
const natural = require('natural');
const aposToLexForm = require('apos-to-lex-form');
const SW = require('stopword');
const SpellCorrector = require('spelling-corrector');
const spellCorrector = new SpellCorrector();
spellCorrector.loadDictionary();
const { WordTokenizer } = natural;
const tokenizer = new WordTokenizer();
const { SentimentAnalyzer, PorterStemmer } = natural;

//Sentiment libaries - 2
var Sentiment = require('sentiment');
var sentiment = new Sentiment();

//Sentiment libraries - 3
const vader = require('vader-sentiment');

//Graphical processing library
const D3Node = require('d3-node');
require('dotenv').config();

//S3
const AWS = require('aws-sdk');
const bucketName = 'jackn1-assignment2-store1';

//Redis
var redis = require('redis');
// Client for subscription
var subscriptionClient = redis.createClient(6379, '432redis.ddns.net');
subscriptionClient.auth("password");
// Client for reading the values from the keys. 
var redisClient = redis.createClient(6379, '432redis.ddns.net');
redisClient.auth("password");


//var addr = require('node-macaddress').one(function (err, addr) { console.log(addr); });
var workerid = Math.floor(Math.random() * 100000);
console.log("Starting with worker id: " + workerid);
redisClient.append('workers', workerid + ',');

subscriptionClient.config('set', 'notify-keyspace-events', 'KEA');
subscriptionClient.subscribe('__keyevent@0__:set');

//This is where the work goes down
subscriptionClient.on('message', function (channel, key) {
  setTimeout(function () {
    redisClient.get(key, function (err, tweetcollection) {
      getMyWork().then(r => {
        if (r != null) {
          for (const filter of r) {
            var storageKey = filter.toLowerCase().replace(/\s/g, '');
            if (key == storageKey) {
              console.log('Doing work - current work load: ' + r.length);
              var result = doWorkAndReturnResult(tweetcollection);
              setS3(storageKey, result).then(k => { }).catch(err => {console.log(err)});
            }
          }
        }
      })
    });
  }, 100);
});

function doWorkAndReturnResult(data) {
  var sentiments = getSentiments(data);
  var chartsvg = getProcessedImage(sentiments);

  return chartsvg;
}

function getMyWork() {
  return new Promise(function (resolve, reject) {
    redisClient.get(workerid, function (err, r) {
      var work = JSON.parse(r);
      resolve(work);
    });
  })
}

function getSentiments(value) {
  const splitter = "SPLITTERRRRRRRRRRRRR"
  const arr = value.split(splitter);
  var filtered = arr.filter(e => e);
  var temp = [
    {
      "name": "Positive",
      "size": 0,
    },
    {
      "name": "Neutral",
      "size": 0,
    },
    {
      "name": "Negative",
      "size": 0,
    }
  ];

  for (const element of filtered) {
    //Apply three sentiment analysis libraries and get quotient of all three
    var a = sentimentalise(element);
    var b = sentiment.analyze(element).comparative;
    var c = vader.SentimentIntensityAnalyzer.polarity_scores(element).compound;
    var k = (a + b + c) / 3

    if (k > 0) {
      temp[0]['size']++;
    } else if (k == 0) {
      temp[1]['size']++;
    } else if (k < 0) {
      temp[2]['size']++;
    }
  }

  return temp;
}

function setS3(key, value) {
  return new Promise(function (resolve, reject) {
    const objectParams = { Bucket: bucketName, Key: key, Body: value };
    const uploadPromise = new AWS.S3({ apiVersion: '2006-03-01' }).putObject(objectParams).promise();
    uploadPromise.then(function (data) {
      console.log("Successfully uploaded data to " + bucketName + "/" + key);
      resolve(true);
    });
  })
}


function sentimentalise(data) {
  const lexedReview = aposToLexForm(data);
  const casedReview = lexedReview.toLowerCase();
  const alphaOnlyReview = casedReview.replace(/[^a-zA-Z\s]+/g, '');

  const tokenizedReview = tokenizer.tokenize(alphaOnlyReview);

  //This was too time expensive, probably has some kind of http implementation
  //  tokenizedReview.forEach((word, index) => {
  //    tokenizedReview[index] =  spellCorrector.correct(word);
  // });

  const filteredReview = SW.removeStopwords(tokenizedReview);


  const analyzer = new SentimentAnalyzer('English', PorterStemmer, 'afinn');
  const analysis = analyzer.getSentiment(filteredReview);

  return analysis;
}



function getProcessedImage(data) {
  return pie({ data: data });
}

function pie({
  data,
  selector: _selector = '#chart',
  container: _container = `
      <div id="container">
        <h2>Pie Chart</h2>
        <div id="chart"></div>
      </div>
    `,
  style: _style = '',
  colorRange: _colorRange = undefined,
  width: _width = 960,
  height: _height = 500,
  radius: _radius = 200
} = {}) {
  const _svgStyles = `
    .arc text {font: 10px sans-serif; text-anchor: middle;  color: 	#FFFF00}
    .arc path {stroke: #fff;}
  `;

  const d3n = new D3Node({
    selector: _selector,
    styles: _svgStyles + _style,
    container: _container
  });

  const d3 = d3n.d3;

  const radius = _radius;

  //const color = d3.scaleOrdinal(_colorRange ? _colorRange : d3.schemeCategory10);
  const color = d3.scaleOrdinal().domain(["positive", "neutral", "negative"])
    .range(["#008000", "#0000FF", "#FF0000"]);

  const arc = d3.arc()
    .outerRadius(radius - 10)
    .innerRadius(0);

  const labelArc = d3.arc()
    .outerRadius(radius - 40)
    .innerRadius(radius - 40);

  const pie = d3.pie()
    .sort(null)
    .value((d) => d.size);

  const svg = d3n.createSVG()
    .attr('width', _width)
    .attr('height', _height)
    .append('g')
    .attr('transform', `translate( ${_radius} , ${_radius} )`);

  const g = svg.selectAll('.arc')
    .data(pie(data))
    .enter().append('g')
    .attr('class', 'arc');

  g.append('path')
    .attr('d', arc)
    .style('fill', (d) => color(d.data.name));

  g.append('text')
    .attr('transform', (d) => `translate(${labelArc.centroid(d)})`)
    .attr('dy', '.35em')
    .text((d) => d.data.name);

  //console.log(d3n.svgString());

  return d3n.svgString();
}

function getAvailableWorkers() {
  return new Promise((resolve, reject) => {
    redisClient.get('workers', function (err, r) {
      var arr = r.split(',');

      var filtered = arr.filter(e => e);
      resolve(filtered);
    });
  });
}

function retireWorker() {
  return new Promise((resolve, reject) => {
    getAvailableWorkers().then(r => {
      var newArr = removeItemOnce(r, workerid);
      console.info(newArr);
      resolve(redisClient.set('workers', newArr.join(',') + ','));
    });
  })
}

function removeItemOnce(arr, value) {
  var index = arr.indexOf(`${value}`);
  if (index > -1) {
    arr.splice(index, 1);
  }
  return arr;
}

process.stdin.resume();//so the program will not close instantly

function exitHandler(options, exitCode) {
  console.log("Attempting to retire worker...");
  retireWorker().then(k=>{
    if (options.cleanup) console.log('clean');
    if (exitCode || exitCode === 0) console.log(exitCode);
    if (options.exit) process.exit();
  });

}

//do something when app is closing
process.on('exit', exitHandler.bind(null, { cleanup: true }));

//catches ctrl+c event
process.on('SIGINT', exitHandler.bind(null, { exit: true }));

// catches "kill pid" (for example: nodemon restart)
process.on('SIGUSR1', exitHandler.bind(null, { exit: true }));
process.on('SIGUSR2', exitHandler.bind(null, { exit: true }));

//catches uncaught exceptions
process.on('uncaughtException', exitHandler.bind(null, { exit: true }));