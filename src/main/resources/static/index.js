const input = document.querySelector('#regex')
input.addEventListener('focus', resetPage)
function resetPage() {
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