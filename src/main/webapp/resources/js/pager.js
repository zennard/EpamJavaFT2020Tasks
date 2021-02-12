let prevLink = document.getElementById('prevPageLink');
let nextLink = document.getElementById('nextPageLink');
let prevLinkUrl = new URL(prevLink.href.valueOf());
let nextLinkUrl = new URL(nextLink.href.valueOf());
initializePrevLink();
initializeNextLink();

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