import {checkPath} from "./redux.util";
describe('Utils', () => {

    describe('checkPath', () => {
        const path = '/tests/detail/Some.Name';
        it('should match the path', () => {
            expect(checkPath(path, [
                /tests/,
                /detail/,
                /.*/
            ])).toBe(true);
        });
        it('should not match with diffenrent sizes', () => {
            expect(checkPath(path, [
                /tests/,
                /detail/
            ])).toBe(false)
        });
        it('it shoul d not match for different content', () => {
            expect(checkPath(path, [
                /^test$/,
                /detail/,
                /.*/
            ])).toBe(false)
        });
    })

    describe('checkPath with array', () => {
        const path = ['/tests']
    })

})