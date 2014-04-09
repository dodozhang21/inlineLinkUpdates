(function($){
    // dialogs
    var editInlineLinkDialogDiv = $('#editInlineLinkDialog');
    var editInlineLinkDialog = $(editInlineLinkDialogDiv).dialog({
        autoOpen: false,
        modal: true,
        width: 500,
        buttons: {
            "Cancel": function() {
                $( this ).dialog( "close" );
            }
            ,
            "Submit": function() {
                var form = $('#editInlineLinkDialog').find('form');
                $.post($(form).attr('action'), $(form).serialize()).fail(function(http) {
                    $(editInlineLinkDialogDiv).html(http.responseText);
                }).done(function(html, textStatus, xhr) {
                    var topicIdEdited = xhr.getResponseHeader('topicId');
                    var topicName = $(html).find('#topicName1').val();
                    var topicUrl = $(html).find('#topicUrl1').val();
                    var priority = $(html).find('#priority1').val();
//                    console.log('topicIdEdited=' + topicIdEdited);
//                    console.log('topicName=' + topicName);
//                    console.log('topicUrl=' + topicUrl);
//                    console.log('priority=' + priority);
                    var editedRow = $('#' + topicIdEdited);
                    $(editedRow).find('.topicName').html(topicName);
                    $(editedRow).find('.topicUrl').html(topicUrl);
                    $(editedRow).find('.priority').html(priority);
                    $(editInlineLinkDialog).dialog("close");
                    $(editedRow).addClass('highlightFade');
                });
            }
        }
    });

    var deleteConfirmationDialog = $('#deleteConfirmationDialog').dialog({
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

    $('a.editInlineLink').on('click', function(e) {
        e.preventDefault();
        $.ajax($(this).attr('href')
            , {
                dataType : 'html'
        }).done(function(html) {
            $(editInlineLinkDialogDiv).html(html);
            $(editInlineLinkDialog).dialog("open");
        });
    });


    $('a.deleteInlineLink').on('click', function(e) {
        e.preventDefault();
        $('#deleteTopicName').html($(this).data('topicname'));
        $(deleteConfirmationDialog).data("link", this).dialog("open");

    });

    $('.resultsPerPage').on('change', function(e) {
        $('#currentPage').val('1');
       $(this).closest('form').submit();
    });

    $('.clearAll').on('click', function(e) {
        e.preventDefault();
        var form = $(this).closest("form");
        $(form).find('input[type="text"], .site select').val('');
    });
})(jQuery);