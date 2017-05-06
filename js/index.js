require('jquery-ui/ui/widgets/dialog.js');
require('jquery-ui/themes/base/core.css');
require('jquery-ui/themes/base/button.css');
require('jquery-ui/themes/base/dialog.css');
require('jquery-ui/themes/base/resizable.css');
require('jquery-ui/themes/base/theme.css');

var jq = require('jquery');

jq(document).on('click', function (event) {

    try {
        console.log(event.target.id);
        var eventId = event.target.id;
        if (eventId && eventId.startsWith("link_")) {
            var id = "#promptMsg_" + eventId.split("_")[1];
            console.log(id);
            var dlg = jq(id).dialog({
                autoOpen: false,
                width: 450,
                height: 300,
                buttons: {
                    'Ok': function () {
                        jq(this).dialog("close");
                    }
                }
            });
            dlg.dialog("open");
        }
    } catch (error) {
        console.log(error);
    }

});