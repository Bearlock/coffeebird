var updateContentDivWithData = function(data){
    document.getElementById('content').innerHTML = JSON.stringify(data);
}



$(document).ready(function(){
    $('#execute_response').click(function(){
       var searchTerm = $('#twitter_terms').val();
        $.ajax({
            url: '/tweet_listener/greeting',
            type: 'POST',
            headers: {
                'Accept':'application/JSON',
                'Content-Type': 'application/JSON'
            },
            dataType: 'json',
            data: {'search_Term': searchTerm },
            success: function(data){updateContentDivWithData(data);},
            error: function(){alert('an error occurred during ajax');}
        });
    });
});