var needle = require('needle');
const express = require('express');
const app = express();
const port = 3001;

var fs = require('fs');
var data = JSON.parse(fs.readFileSync('testdata.json'));
console.log(data.length);


var index = 0;
app.get('/test', function (req, res) {
    res.header('Content-Type', 'text/event-stream');

    var interval_id = setInterval(function () {
        console.log("responsing with data... " + index);
        
        res.write(JSON.stringify(data[index]));
        if (index == data.length - 1) {
            index = 0;
        } else {
            index++;
        }
    },100);

    req.socket.on('close', function () {
        clearInterval(interval_id);
    });
});


app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})




// var index = 0;
// function doStuff() {
//     console.log(data[index]);
//     if (index == data.length - 1) {
//         index = 0;
//     } else {
//         index++;
//     }
// }
// setInterval(doStuff, 43); //time is in ms