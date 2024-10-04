export function abc() {
    console.log("hello world");
}

export function assertE2eErrorEquals(
    expectedStatusCode,
    expectedErrorCode,
    expectedMessage
) {
    client.test("응답이 status는 404이다", () => {
        client.assert(response.status === expectedStatusCode, '응답의 status가 404가 아닙니다')
    })

    client.test("응답의 body는 error를 포함한다", () => {
        const error = response.body.error
        client.assert(error !== null)
        client.assert(error.code === expectedErrorCode)
        client.assert(error.message === expectedMessage)
    })
}

export function assertE2eDataEquals(
    expectedItemId,
    expectedStock
) {
    client.test("응답이 status는 200이다", () => {
        client.assert(response.status === 200, '응답의 status가 200가 아닙니다')
    })

    client.test("응답의 body는 error를 포함한다", () => {
        const data = response.body.data
        client.assert(data !== null)
        client.assert(data.itemId === expectedItemId)
        client.assert(data.stock !== null && data.stock > 0)

        if (expectedStock) {
            client.assert(data.stock === expectedStock)
        }
    })
}
