//Used imports
require('dotenv').config();
const needle = require('needle');
const express = require('express')
const app = express()
const cors = require('cors');
const port = 3000;




//#region no-ip, this will ensure the endpoint address remains the same
var NoIP = require('no-ip');


var noip = new NoIP({
    hostname: 'cab432server.ddns.net',
    user: 'n10712721@qut.edu.au',
    pass: 'Watermelon.5'
})

noip.on('error', function (err) {
    console.log(err)
})

noip.on('success', function (isChanged, ip) {
    console.log(isChanged, ip)
})

// noip.update()
//#endregion

//Twitter api fields
const token = process.env.BEARER_TOKEN;
const rulesURL = 'https://api.twitter.com/2/tweets/search/stream/rules';
//const streamURL = 'http://localhost:3001/test/'
const streamURL = 'https://api.twitter.com/2/tweets/search/stream';

//Redis
const redis = require('redis');
const redisClient = redis.createClient(6379, '432redis.ddns.net');
redisClient.auth("password");



//S3
const AWS = require('aws-sdk');
const bucketName = 'jackn1-assignment2-store1';

//Initialise
function initialise() {
    //Update cache with current live filters
    getRulesAndFormatForCache().then((r) => {
        var activefilters = JSON.stringify(r);

        redisClient.set('activefilters', activefilters);

        getAllFilters().then(r => {
            console.info(r);

            //Clear workers
            //redisClient.set('workers', '');
        });
    });
}
initialise();

//#region routes
app.use(cors({
    origin: '*'
}));


//Get data for a filter
app.get('/getfilter/:name', (req, res) => {
    res.header("Access-Control-Allow-Origin", "*"); res.header("Access-Control-Allow-headers", "Origin, X-Requested-With, Content-Type, Accept");

    var name = req.params.name.toLowerCase().replace(/\s/g, '');
    console.log("Requesting " + name);

    getS3(name).then(k => {
        res.send(k);
    });
})

//Get all filters
app.get('/getallfilters', (req, res) => {
    res.header("Access-Control-Allow-Origin", "*"); res.header("Access-Control-Allow-headers", "Origin, X-Requested-With, Content-Type, Accept");

    console.log("Attempting to get all filters..");
    getAllFilters().then(k => {
        res.json(k);
    });
})

//Submit a new filter
app.get('/submitfilter/:name', (req, res) => {
    res.header("Access-Control-Allow-Origin", "*"); res.header("Access-Control-Allow-headers", "Origin, X-Requested-With, Content-Type, Accept");

    var name = req.params.name.toString();
    var rule = formRule(name);

    getAllRules().then((r) => {
        if (r.meta.result_count < 25) {
            addRule(rule).then((a) => {
                redisClient.get('activefilters', function (err, data) {
                    var parsed = JSON.parse(data);
                    parsed.push([name, false]);
                    redisClient.set('activefilters', JSON.stringify(parsed));
                    res.send(a);
                });
            }).catch((e) => { console.log(e) });
        } else {
            res.send("The max amount of filters are already being tracked")
        }
    });
})

//Activate a filter
app.get('/activatefilter/:name', (req, res) => {
    res.header("Access-Control-Allow-Origin", "*"); res.header("Access-Control-Allow-headers", "Origin, X-Requested-With, Content-Type, Accept");

    //need to check if exists in cache first
    var name = req.params.name.toString();

    var rule = formRule(name);

    getAllRules().then((r) => {
        if (r.meta.result_count < 25) {
            addRule(rule).then((a) => {
                redisClient.get('activefilters', function (err, data) {
                    var parsed = JSON.parse(data);
                    parsed.push([name, false]);
                    redisClient.set('activefilters', JSON.stringify(parsed));

                    removeFromInactiveCacheFilters(name).then(c => {
                        res.send(a);
                    });

                });
            }).catch((e) => { console.log(e) });
        } else {
            res.send("The max amount of filters are already being tracked")
        }
    });
})

//Deactivate a filter
app.get('/deactivatefilter/:name', (req, res) => {
    res.header("Access-Control-Allow-Origin", "*"); res.header("Access-Control-Allow-headers", "Origin, X-Requested-With, Content-Type, Accept");

    var name = req.params.name.toString();

    deleteARule(name).then(r => {
        removeFromActiveCacheFilters(name).then(a => {
            addToInactiveCacheFilters(name).then(b => {
                res.send([r, a, b]);
            }).catch(function (reason) {
                console.log('deletefrominactivefilters: ' + reason);// rejection
            });
        }).catch(function (reason) {
            console.log('removefromactivefilters: ' + reason);// rejection
        });
    }).catch(function (reason) {
        console.log('deletearule: ' + reason);// rejection
    });
})

//Delete a filter
app.get('/deletefilter/:name', (req, res) => {
    res.header("Access-Control-Allow-Origin", "*"); res.header("Access-Control-Allow-headers", "Origin, X-Requested-With, Content-Type, Accept");

    var name = req.params.name.toString().toLowerCase();
    console.log(name);
    removeFromActiveCacheFilters(name).then(a => {
        if (a == true) {
            console.log("Successfully removed from active cache... now deleting rule");
            deleteARule(name).then((h) => {
                res.send(h);
            });
        } else {
            removeFromInactiveCacheFilters(name).then(b => {
                console.log("from inactive");
                res.send(b);
            }).catch(function (err) {
                res.send("Error removing it anywhere");
            });
        }
    }).catch(function (err) {
        res.send(err);
    });
})

function getAllFilters() {
    return new Promise((resolve, reject) => {
        redisClient.get('activefilters', function (err, r) {
            var activefilters = r == '' ? '[]' : r;
            redisClient.get('inactivefilters', function (err, data) {
                var inactivefilters = data == '' ? '[]' : data;

                var obj = { 'active': activefilters, 'inactive': inactivefilters };
                resolve(obj);
            });
        });
    })
}

function getActiveFiltersFromCache() {
    return new Promise((resolve, reject) => {
        redisClient.get('activefilters', function (err, r) {
            var activefilters = r == '' ? '[]' : r;
            resolve(activefilters);
        });
    });
}

function getInactiveFiltersFromCache() {
    return new Promise((resolve, reject) => {
        redisClient.get('inactivefilters', function (err, r) {
            var inactivefilters = r == '' ? '[]' : r;
            resolve(inactivefilters);
        });
    });
}


function removeFromActiveCacheFilters(filter) {
    return new Promise((resolve, reject) => {
        getActiveFiltersFromCache().then(h => {
            var active = JSON.parse(h);
            var found = active.map(function (e) { return e[0].toLowerCase(); }).indexOf(filter.toLowerCase());
            if (found == -1) resolve(false);
            var item = active[found]
            var itemremoved = removeItemOnce(active, item);

            resolve(redisClient.set('activefilters', JSON.stringify(itemremoved)));
        });
    });
}


function addToActiveCacheFilters(filter) {
    return new Promise((resolve, reject) => {
        getActiveFiltersFromCache().then(h => {
            var active = JSON.parse(h);
            active.push([filter, false]);

            resolve(redisClient.set('activefilters', JSON.stringify(active)));
        });
    });
}

function removeFromInactiveCacheFilters(filter) {
    return new Promise((resolve, reject) => {
        getInactiveFiltersFromCache().then(h => {
            var inactive = JSON.parse(h);
            var found = inactive.map(function (e) { return e[0].toLowerCase(); }).indexOf(filter.toLowerCase());
            if (found == -1) resolve(false);
            var item = inactive[found]
            var itemremoved = removeItemOnce(inactive, item);

            resolve(redisClient.set('inactivefilters', JSON.stringify(itemremoved)));
        });
    });
}

function addToInactiveCacheFilters(filter) {
    return new Promise((resolve, reject) => {
        getInactiveFiltersFromCache().then(h => {
            var inactive = JSON.parse(h);
            inactive.push([filter, false]);

            resolve(redisClient.set('inactivefilters', JSON.stringify(inactive)));
        });
    });
}

//Listen for connections
app.listen(port, () => {
    console.log(`http://localhost:${port}/getfilter/testing`);
    console.log(`http://cab432server.ddns.net:${port}/getfilter/testing`);
    console.log(`App is now listening on port: ${port}`)
})
//#endregion


function getS3(key) {
    return new Promise(function (resolve, reject) {
        const params = { Bucket: bucketName, Key: key };
        const getpromise = new AWS.S3({ apiVersion: '2006-03-01' }).getObject(params).promise();

        getpromise.then(function (data) {
            console.log("Successfully retrieved data from bucket: " + key);
            resolve(data.Body.toString('utf8'));
        });
    })
}

async function getRulesAndFormatForCache() {
    var r = await getAllRules();
    var temp = [];
    for (const element of r.data) {
        temp.push([element['tag'], false])

    }
    return temp;
}


function removeItemOnce(arr, value) {
    var index = arr.indexOf(value);
    if (index > -1) {
        arr.splice(index, 1);
    }
    return arr;
}

function formRule(value) {
    return [{ 'value': `"${value}" has:mentions`, 'tag': `${value}` }]
}

// const rules = [
//     { 'value': '"Abortion" has:mentions', 'tag': "Abortion" },
//     { 'value': '"Affirmative Action" has:mentions', 'tag': "Affirmative Action" },
//     { 'value': '"Education" has:mentions', 'tag': "Education" },
//     { 'value': '"Education" has:mentions', 'tag': "Education" },
//     { 'value': '"Health" has:mentions', 'tag': "Health" },
//     { 'value': '"Interpersonal Communication" has:mentions', 'tag': "Interpersonal Communication" },
//     { 'value': '"Barack Obama" has:mentions', 'tag': "Barack Obama" },
//     { 'value': '"Discrimination" has:mentions', 'tag': "Discrimination" },
//     { 'value': '"Schools" has:mentions', 'tag': "Schools" },
//     { 'value': '"AIDS" has:mentions', 'tag': "AIDS" },
//     { 'value': '"Alcohol" has:mentions', 'tag': "Alcohol" },
//     { 'value': '"Animals" has:mentions', 'tag': "Animals" },
//     { 'value': '"Athletes" has:mentions', 'tag': "Athletes" },
//     { 'value': '"Censorship" has:mentions', 'tag': "Censorship" },
//     { 'value': '"Bill Clinton" has:mentions', 'tag': "Bill Clinton" },
//     { 'value': '"Hilary Clinton" has:mentions', 'tag': "Hilary Clinton" },
//     { 'value': '"College" has:mentions', 'tag': "College" },
//     { 'value': '"Hacking" has:mentions', 'tag': "Hacking" },
//     { 'value': '"Copyright" has:mentions', 'tag': "Copyright" },
//     { 'value': '"Cosmetics" has:mentions', 'tag': "Cosmetics" },
//     { 'value': '"Bullying" has:mentions', 'tag': "Bullying" },
//     { 'value': '"Exciting" has:mentions', 'tag': "Exciting" },
//     { 'value': '"Justice" has:mentions', 'tag': "Justice" },
//     { 'value': '"Date rape" has:mentions', 'tag': "Date rape" },
//     { 'value': '"Day care" has:mentions', 'tag': "Day care" },
// ];



//Functions/variables for handling the streamed in data

function getAvailableWorkers() {
    return new Promise((resolve, reject) => {
        redisClient.get('workers', function (err, r) {
            var arr = r.split(',');
            var filtered = arr.filter(e => e);
            resolve(filtered);
        });
    });
}

function allocatework() {
    return new Promise((resolve, reject) => {
        getActiveFiltersFromCache().then(r => {
            getAvailableWorkers().then(workers => {
                var activefilters = JSON.parse(r);
                var totalfilters = activefilters.length;
                var availableworkers = workers.length;
                var workload = parseInt((totalfilters / availableworkers)   +  0);

                work = [];
                count = 0;
                workerIndex = 0;
                index = 0;
                if (availableworkers > 0) {
                    for (const filter of activefilters) {
                        if (count < workload && filter != activefilters[activefilters.length - 1] || (totalfilters-index) < parseInt((workload * 1.5))  && filter != activefilters[activefilters.length - 1]) {
                            work.push(filter[0]);
                            count++;
                        } else {
                            work.push(filter[0]);
                            redisClient.set(workers[workerIndex], JSON.stringify(work));
                            console.log('Allocating work to ' + workers[workerIndex] + ', number of workers: '+ availableworkers);
                            work = [];
                            count = 0;
                            workerIndex++
                        }
                        index++;
                    }
                    resolve(true);
                } else {
                    resolve(false);
                }
            });
        })
    });
}

//Modify this to increase the load
const maxtweetstore = 4;

//Handle the twitter data (this is called on each item of data)
//With 25 rules ~23 tweets/second or 1 tweet every 0.45ms
var count = 0;
// function handleTwitterData(data) {
//     //---------- Data operations ------------
//     if (count > 10) {//Do every 10 tweets
//         //allocatework
//         allocatework().then(k => { });
//         count = 0;
//     }

//     const json = JSON.parse(data);

//     const splitter = "SPLITTERRRRRRRRRRRRR"
//     var key = json.matching_rules[0].tag.toLowerCase().replace(/\s/g, '');
//     var value = json.data.text;

//     //console.log(value);

//     redisClient.get(key, function (err, cachedata) {
        
//         var arr = []; 
//         if(cachedata != null){
//             arr = cachedata.split(splitter);
//         }
//         const length = arr.length;



//         var returnVal = "";
//         const finalindex = length - 1;
//         if (length >= maxtweetstore) {
//             var partialArr = arr.splice(finalindex - maxtweetstore + 1, finalindex);
//             var filtered = partialArr.filter(e => e);
//             filtered.push(value)

//             returnVal = filtered.join(splitter);
//         } else {
//             returnVal = cachedata + value;
//         }

//         redisClient.set(key, returnVal + splitter);
//         count++;
//     });

//     //-------------------------------
//     //Write to file
//     // if (obj.length == 500) {
//     //     console.log("Writing to file");
//     //     var tablejson = JSON.stringify(obj);
//     //     fs.writeFile("testdata.json", tablejson, function (err) {
//     //         if (err) {
//     //             return console.log(err);
//     //         }
//     //         console.log("The file was saved!");
//     //     });
//     // }
//     // A successful connection resets retry count.
// }

//#region stream in twitter data

async function getAllRules() {

    const response = await needle('get', rulesURL, {
        headers: {
            "authorization": `Bearer ${token}`
        }
    })

    if (response.statusCode !== 200) {
        console.log("Error:", response.statusMessage, response.statusCode)
        throw new Error(response.body);
    }

    return (response.body);
}

async function deleteAllRules(rules) {

    if (!Array.isArray(rules.data)) {
        return null;
    }

    const ids = rules.data.map(rule => rule.id);

    const data = {
        "delete": {
            "ids": ids
        }
    }

    const response = await needle('post', rulesURL, data, {
        headers: {
            "content-type": "application/json",
            "authorization": `Bearer ${token}`
        }
    })

    if (response.statusCode !== 200) {
        throw new Error(response.body);
    }

    return (response.body);

}

async function addRule(rule) {

    const data = {
        "add": rule
    }

    const response = await needle('post', rulesURL, data, {
        headers: {
            "content-type": "application/json",
            "authorization": `Bearer ${token}`
        }
    })

    if (response.statusCode !== 201) {
        throw new Error(response.body);
    }

    return (response.body);

}

async function deleteARule(name) {

    var rules = await getAllRules();

    if (!Array.isArray(rules.data)) {
        return null;
    }

    const ids = [];
    for (const rule of rules.data) {
        if (rule.tag.toLowerCase() == name.toLowerCase()) {
            ids.push(rule.id);
        }
    }

    const data = {
        "delete": {
            "ids": ids
        }
    }

    const response = await needle('post', rulesURL, data, {
        headers: {
            "content-type": "application/json",
            "authorization": `Bearer ${token}`
        }
    })

    if (response.statusCode !== 200) {
        throw new Error(response.body);
    }

    return (response.body);

}

async function setRules() {

    const data = {
        "add": rules
    }

    const response = await needle('post', rulesURL, data, {
        headers: {
            "content-type": "application/json",
            "authorization": `Bearer ${token}`
        }
    })

    if (response.statusCode !== 201) {
        throw new Error(response.body);
    }

    return (response.body);

}



function streamConnect(retryAttempt) {
    const stream = needle.get(streamURL, {
        headers: {
            "User-Agent": "v2FilterStreamJS",
            "Authorization": `Bearer ${token}`
        },
        timeout: 20000
    });

    stream.on('data', data => {
        try {
            console.info(JSON.parse(data));
            //handleTwitterData(data);
            retryAttempt = 0;
        } catch (e) {
            if (data.detail === "This stream is currently at the maximum allowed connection limit.") {
                console.log(data.detail)
                process.exit(1)
            } else {
                // Keep alive signal received. Do nothing.
            }
        }
    }).on('err', error => {
        if (error.code !== 'ECONNRESET') {
            console.log(error.code);
            process.exit(1);
        } else {
            // This reconnection logic will attempt to reconnect when a disconnection is detected.
            // To avoid rate limits, this logic implements exponential backoff, so the wait time
            // will increase if the client cannot reconnect to the stream. 
            setTimeout(() => {
                console.warn("A connection error occurred. Reconnecting...")
                streamConnect(++retryAttempt);
            }, 2 ** retryAttempt)
        }
    });
    return stream;
}

(async () => {
    let currentRules;

    try {
        // Gets the complete list of rules currently applied to the stream
        // currentRules = await getAllRules();
        // console.info(currentRules);
        // // Delete all rules. Comment the line below if you want to keep your existing rules.
        // await deleteAllRules(currentRules);

        // // Add rules to the stream. Comment the line below if you don't want to add new rules.
        // await setRules();

    } catch (e) {
        console.error(e);
        process.exit(1);
    }

    // Listen to the stream.
    streamConnect(0);
})();

//#endregion
