let showDynaCoverage2CallBack = function dynaCoverage2(evt) {
    let features = highlightOverlay.getSource().getFeatures();
    let element = popup.getElement();
    console.log(features)
    if (features.length) {
        popup.setPosition(evt.coordinate);
        // the keys are quoted to prevent renaming in ADVANCED mode.
        $(element).popover({
            'placement': 'top',
            'animation': false,
            'html': true,
            'content': createPopupContent(features)
        });
        $(element).popover('show');
        $('.table-popup > tbody > tr').click(function () {
            // row was clicked
            let cell = $(this).find('td:first');
            showDynaCoverage2(cell.text(), cell.data("lon"), cell.data("lat"));
        });
    }
};