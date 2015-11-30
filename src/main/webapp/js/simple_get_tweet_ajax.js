var updateContentDivWithData = function(data){
    document.getElementById('content').innerHTML = JSON.stringify(data);
}



$(document).ready(function(){
    $('#execute_response').click(function(){
       var searchTerm = $('#twitter_terms').val();
       console.log(searchTerm);
        $.ajax({
            url: '/tweet_listener/greeting',
            type: 'POST',
            headers: {
                'Accept':'application/json',
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            dataType: 'json',
            data: {'search_Term': searchTerm },
            success: function(data){updateContentDivWithData(data);},
            error: function(){alert('an error occurred during ajax');}
        });
    });
});