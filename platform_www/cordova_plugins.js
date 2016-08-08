cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "cordova-plugin-whitelist.whitelist",
        "file": "plugins/cordova-plugin-whitelist/whitelist.js",
        "pluginId": "cordova-plugin-whitelist",
        "runs": true
    },
    {
        "id": "com.evontech.videoplugin.VideoPlugin",
        "file": "plugins/com.evontech.videoplugin/www/VideoPlugin.js",
        "pluginId": "com.evontech.videoplugin",
        "clobbers": [
            "VideoPlugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.2.1",
    "com.evontech.videoplugin": "0.7.0"
};
// BOTTOM OF METADATA
});