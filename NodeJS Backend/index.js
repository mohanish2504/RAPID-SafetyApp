const functions = require('firebase-functions');
const admin = require('firebase-admin');
const geofire = require('geofire');

admin.initializeApp(functions.config().firebase);

exports.Notifications = functions.database.ref('/Triggers/{DateTime}/').onWrite((change,context) => {

    const datetime = change.after.val(); //getting locations from triggering device
    if(datetime === null) return true;
    const number = datetime['mobile']
    const Locations = datetime['location']
    const emergencycontacts = datetime['emergencyContacts']

    const time = change.after.key
    //console.log(time)

    var lat = Locations.latitude;
    var lon = Locations.longitude;

    var ref = admin.database().ref('NewLocations');

    var Tokens
    var tokensdb = admin.database().ref('DeviceTokens')
    return tokensdb.once('value').then(snapshot =>{
        Tokens = snapshot.val()
        var UserDetails
        var userdb = admin.database().ref('UserDetails')
        return userdb.once('value').then(snapshot =>{
             UserDetails = snapshot.val()

             var tokens = [];
             var contactstokens = [];

             if(typeof(emergencycontacts)!=="undefined"){
                 for(var i = 0;i<emergencycontacts.length;i++){
                       if(typeof(Tokens[emergencycontacts[i]])!=="undefined")contactstokens.push(Tokens[emergencycontacts[i]])
                 }
             }

             var geofireinstance = new geofire.GeoFire(ref);
             var geoquery = geofireinstance.query({
               center : [lat,lon],
               radius : 3
             });

             var nearbyUsers = [];

             var onKeyEntered =  geoquery.on("key_entered",function(key,location){
                     if(key!==number && !(contactstokens.includes(Tokens[key])) ){
                        //console.log(key)
                        nearbyUsers.push(key)
                        tokens.push(Tokens[key]);
                     }
             });
             var onReady = geoquery.on("ready", function() {
                     lat = lat.toString();
                     lon = lon.toString();
                     UserDetails[number]['lat'] = lat;
                     UserDetails[number]['lon'] = lon;
                     UserDetails[number]['time'] = time;
                     if(contactstokens.length>0) sendToContacts(Object.assign({}, UserDetails[number]),contactstokens,emergencycontacts);
                     if(tokens.length>0) sendToTokens(Object.assign({}, UserDetails[number]),tokens,nearbyUsers);
                     geoquery.cancel();
             });
             //console.log("Success")
             return true
        })
    }).catch(e=>{

    })
})

function sendToTokens(Data,tokens,nearbyUsers){
  var userData = Data
  userData['title'] = '!!! User is in trouble !!!';
  userData['body'] = userData['firstName'] +' has requested for help.';
  userData['alert'] = 'false'
  const payload = {
               data:userData
  };
   return admin.messaging().sendToDevice(tokens,payload).then(response => {
           return console.log(' Notification Sent to Nearby Users ' + nearbyUsers);
   });
}

function sendToContacts(Data,contactstokens,emergencycontacts){
   var userData = Data
   userData['title'] = '!!! Your contact requires attention !!!';
   userData['body'] = 'Reach out to '+ userData['firstName'] + ' as quickly as possible '
   userData['alert'] = 'true'
   //console.log('alert here is true and contacts are :: ' + emergencycontacts)
    const payload = {
                   data:userData
    };
    return admin.messaging().sendToDevice(contactstokens,payload).then(response => {
               return console.log('Notification Sent to Contacts ' + emergencycontacts);
    });
}