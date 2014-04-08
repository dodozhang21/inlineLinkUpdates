(function($){
    var deleteConfirmationDialog = $('#deleteConfirmation').dialog({
        autoOpen: false,
        modal: true,
        buttons: {
            "Yes, I'm sure!": function() {
                $(this).dialog("close");
                window.location = $(this).data('link').href;
            }
            ,
            "Cancel": function() {
                $( this ).dialog( "close" );
            }
        }
    });
    $('a.deleteInlineLink').on('click', function(e) {
        e.preventDefault();
        $('#deleteTopicName').html($(this).data('topicName'));
        $(deleteConfirmationDialog).data("link", this).dialog("open");

    });

    $('.clearAll').on('click', function(e) {
        e.preventDefault();
        var form = $(this).closest("form");
        $(form).find('input[type="text"], select').val('');
    });
})(jQuery);