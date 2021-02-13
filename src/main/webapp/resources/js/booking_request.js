const submitForm = document.submitForm;
const table = document.getElementById("apartmentsTable");
let selectedIds = JSON.parse(localStorage.getItem("selectedIds"));

if (!selectedIds) {
    selectedIds = [];
}

renderCheckboxes();
initializeLinkListeners()

function initializeLinkListeners() {
    const elements = document.getElementsByTagName('a');
    for (let i = 0, len = elements.length; i < len; i++) {
        if (!elements[i].classList.contains("page-link")) {
            elements[i].addEventListener("click", () => {
                console.log("clearing")
                localStorage.removeItem("selectedIds");
            });
        }
    }
}

function renderCheckboxes() {
    let rows = table.querySelectorAll("tr");
    for (let i = 0; i < rows.length; i++) {
        const checkboxTableData = rows[i].querySelector("td");
        if (checkboxTableData) {
            const checkbox = checkboxTableData.querySelector("input");
            const currentApartmentId = checkbox.dataset.apartmentId;
            if (selectedIds.filter(id => id === currentApartmentId).length > 0) {
                checkbox.checked = true;
            }
        }
    }
}

function saveChoice() {
    const currentId = event.target.dataset.apartmentId;
    if (event.target.checked) {
        selectedIds.push(currentId);
    } else {
        selectedIds = selectedIds.filter(id => id !== currentId)
    }
    localStorage.setItem("selectedIds", JSON.stringify(selectedIds));
}

function onSubmit() {
    for (let i = 0; i < selectedIds.length; i++) {
        const inputElem = createInput(i);
        submitForm.appendChild(inputElem);
    }
    localStorage.removeItem("selectedIds");
    submitForm.submit();
}

function createInput(valueIndex) {
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "apartmentIds";
    input.value = selectedIds[valueIndex];
    return input;
}