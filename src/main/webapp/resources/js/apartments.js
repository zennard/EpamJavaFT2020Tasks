const sortForm = document.sortForm;
const sortParam = new URL(location.href).searchParams.get('sort');

initializePrevLinkWithSortParam();
initializeNextLinkWithSortParam();

function onApartmentClick() {
    console.log(event.target);
    console.log(event.target.getAttribute('action'));
    const url = new URL('http://localhost:8080' + event.target.getAttribute('action'));
    console.log(url.toString())
    console.log(checkInInput.value);
    url.searchParams.set('startsAt', checkInInput.value);
    url.searchParams.set('endsAt', checkOutInput.value);
    window.location = url;
}

function onSortButtonClick() {
    const sortType = event.target.getAttribute('data-type');

    const url = new URL(location.href);

    const search_params = url.searchParams;
    search_params.set('sort', sortType);
    url.search = search_params.toString();

    window.history.pushState("s", "", `${url.toString()}`);

    resultUrl.searchParams.set('sort', sortType);

    sortForm.action = resultUrl.toString();
    document.getElementById('sortTypeValue').value = sortType;
    document.getElementById('sortStartsAt').value = checkInInput.value;
    document.getElementById('sortEndsAt').value = checkOutInput.value;
    sortForm.submit();
}

function initializePrevLinkWithSortParam() {
    if (sortParam != null) {
        prevLinkUrl.searchParams.set('sort', sortParam);
    }
    prevLink.href = prevLinkUrl.toString();
}

function initializeNextLinkWithSortParam() {
    if (sortParam != null) {
        nextLinkUrl.searchParams.set('sort', sortParam);
    }
    nextLink.href = nextLinkUrl.toString();
}