var express = require('express');
var app = express();
var path = require('path');
var bodyParser = require('body-parser');
var cookieParser = require('cookie-parser');
var session = require('express-session');
var crypto = require('crypto');
var server = require('http').createServer(app);
var io = require('socket.io').listen(server)

// MySQL Connector
var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  database : 'PublicSafetyApp',
  user     : 'root',
  password : ''
});

var port = process.env.PORT || 8070;
server.listen(port);

app.use(function (req, res, next) {
    res.setHeader('Access-Control-Allow-Origin', 'http://dev.publicsafetyapp.com');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
    res.setHeader('Access-Control-Allow-Credentials', true);

    // Pass to next layer of middleware
    next();
});

console.log("Running");

//app.use(bodyParser());

// User Login
app.post('/api/login',function(req,res){
	var pw = req.body.password;
	var hashedPass = crypto.createHash('md5').update(pw).digest("hex");
		
	connection.query('SELECT u.UserID, u.UserType, r.OrganizationID FROM User u WHERE u.Login = "' + req.body.login + '" AND u.Password = "' + hashedPass + '" LEFT JOIN Responder r ON u.UserID = r.UserID', function(err, rows, fields) {
		if (err) throw err;
		if(rows.length > 0){
			//log user in
			req.session.OrganizationID = rows[0].OrganizationID;
			req.session.Role = rows[0].UserType;
			req.session.UserID = rows[0].UserID;
			res.send(200);
		}else{
			//login error
			res.send(422);
		}
	});
});

// Need to check auth before executing api methods below
// Need to sanitize user inputs

// Create Event
app.post('/api/event/create', function(req,res){
	var eventName = req.body.EventName;
	var eventDescription = req.body.EventDescription;
	connection.query('INSERT INTO Event (EventName, EventDescription) VALUES ( "' + eventName + '","' + eventDescription + '")', function(err, result) {
		if (err) throw err;
		// create event
		// add event id to session
		req.session.EventID = result.insertID;
		res.send({status:200,EventID:result.insertID});
		// otherwise
		//event creation error
		//res.send(ERROR CODE);
		
	});
});

// Get Responders
app.get('/api/responder/list',function(req,res){
	connection.query('SELECT r.ResponderID, r.FirstName, r.LastName FROM Responder r WHERE r.OrganizationID = ' + req.query.OrganizationID, function(err, rows, fields) {
		if (err) throw err;
		res.send(rows);
	});
});

// Add Responder to Roster
app.post('/api/roster/add', function(req,res){
	var responderID = req.body.ResponderID;
	var eventID = req.body.RequestID;
	
	connection.query('INSERT INTO ResponderEvent (ResponderID, EventID) VALUES ( ' + responderID + ',' + eventID + ')', function(err, result) {
		if (err) throw err;
		res.send({status:200});
	});
});

// Activate Responder
app.post('/api/responder/activate',function(req,res){
	var eventResponder = req.body.RequestID;
	var actionTypeID = 1;
	
	connection.query('INSERT INTO ActivityLog (EventResponderID, ActionTypeID) VALUES ( ' + eventResponder + ',' + ActionTypeID + ')', function(err, rows, fields) {
		if (err) throw err;
		
	});
});

// Deactivate Responder
app.post('/api/responder/deactivate',function(req,res){
	var eventResponder = req.body.RequestID;
	var actionTypeID = 2;
	
	connection.query('INSERT INTO ActivityLog (EventResponderID, ActionTypeID) VALUES ( ' + eventResponder + ',' + ActionTypeID + ')', function(err, rows, fields) {
		if (err) throw err;
	});
});

// Log Responder Activity
app.post('/api/responder/activity',function(req,res){
	var eventResponder = req.body.RequestID;
	var activityTypeID = req.body.ActivityTypeID;
	var activityValue = req.body.ActivityValue;
	
	connection.query('INSERT INTO ActivityLog (EventResponderID, ActionTypeID, ActivityValue) VALUES (' + eventResponder + ',' + ActionTypeID + ',"' + activityValue + '")', function(err, rows, fields) {
		if (err) throw err;
	});
});

// Set Responder Movement
app.post('api/responder/set-movement',function(req,res){
	var responderID = req.body.ResponderID;
	var eventID = req.body.EventID;
	var movement = req.body.Movement;
	connection.query('INSERT INTO Movement (ResponderID, EventID, Movement) VALUES ( ' + responderID + ',' + eventID + ',"' + activityValue + ',' + movement + ')', function(err, rows, fields) {
		if (err) throw err;
	});
});

// Get Responder Movement
app.post('api/responder/get-movement',function(req,res){
	var eventID = req.body.EventID;
	connection.query('SELECT m.ResponderID, m.EventID, m.Movement, m.Time FROM Movement m WHERE m.EventID = ' + eventID,function(err, rows, fields){
		//send responder movements
		res.send(rows);
	});
});

/*
//THIS IS IF WE DID THROUGH SOCKETS
var responders = [];
io.sockets.on('connection',function(socket){
	//register responder
	socket.on('registerResponder',function(data){
		responders.push(data.ResponderID);
		console.log(responders);
	});
	
	
	
});
*/

