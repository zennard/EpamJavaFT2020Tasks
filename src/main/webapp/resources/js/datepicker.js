const checkInInput = document.getElementById('checkIn');
const checkOutInput = document.getElementById('checkOut');
const searchBtn = document.getElementById('searchBtn');
const dateForm = document.dateForm;
let prevLink = document.getElementById('prevPageLink');
let nextLink = document.getElementById('nextPageLink');

const startsAt = new URL(location.href).searchParams.get('startsAt');
const endsAt = new URL(location.href).searchParams.get('endsAt');
let resultUrl = initializeUrl();

let prevLinkUrl = new URL(prevLink.href.valueOf());
let nextLinkUrl = new URL(nextLink.href.valueOf());
initializePrevLink();
initializeNextLink();


onDateChange(checkInInput, 'startsAt');
onDateChange(checkOutInput, 'endsAt');
onButtonClick(searchBtn);

updateDateValue(checkInInput);
updateDateValue(checkOutInput);

function onDateChange(elem, param) {
    elem.onchange = function (e) {
        const value = e.target.value;
        const url = new URL(location.href);

        updateDateValue(e.target);

        const search_params = url.searchParams;
        search_params.set(param, value);
        url.search = search_params.toString();

        window.history.pushState("s", "", `${url.toString()}`);
        resultUrl.searchParams.set(param, value);
    }
}

function onButtonClick(elem) {
    elem.onclick = function (e) {
        console.log(resultUrl.toString())
        dateForm.action = resultUrl.toString();
        console.log(dateForm.action)
        dateForm.submit();
    }
}

function initializeUrl() {
    const url = new URL(location.href);
    url.searchParams.set('page', '0');
    url.searchParams.set('size', '2');
    url.searchParams.set('startsAt', '');
    url.searchParams.set('endsAt', '');
    return url;
}

function initializePrevLink() {
    if (startsAt != null) {
        prevLinkUrl.searchParams.set('startsAt', startsAt);
    }
    if (endsAt != null) {
        prevLinkUrl.searchParams.set('endsAt', endsAt);
    }
    prevLink.href = prevLinkUrl.toString();
}

function initializeNextLink() {
    if (startsAt != null) {
        nextLinkUrl.searchParams.set('startsAt', startsAt);
    }
    if (endsAt != null) {
        nextLinkUrl.searchParams.set('endsAt', endsAt);
    }
    nextLink.href = nextLinkUrl.toString();
}

function updateDateValue(elem) {
    elem.setAttribute("data-date", moment(elem.value, "YYYY-MM-DD")
        .format(elem.getAttribute("data-date-format")));
}
