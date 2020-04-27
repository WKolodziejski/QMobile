javascript:(function() {
    var targetNode = document.getElementById('ctl00_UpdateProgressAguarde');
    var config = {attributes: true};
    var callback = function(mutationsList, observer) {
            for(var mutation of mutationsList) {
                if (mutation.attributeName == 'aria-hidden') {
                    if (mutation.target.attributes['aria-hidden'].value == 'true') {
                        console.log(mutation.attributeName + ' ' + mutation.target.attributes['aria-hidden'].value);
                    }
                }
        	}
        };
        var observer = new MutationObserver(callback);
        observer.observe(targetNode, config);
})()