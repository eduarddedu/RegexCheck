const inputs = [document.querySelector('#regex'), document.querySelector('#text')]
inputs.forEach(input => input.addEventListener('focus', clearPage))

function clearPage() {
    hideSaveRegexCard()
    clearMatchers()
}
function hideSaveRegexCard() {
    const card = document.querySelector('#save-regex-card')
    card.style = 'visibility:hidden'
}
function clearMatchers() {
    const textArea = document.querySelector('#matchers')
    textArea.value = ''
}