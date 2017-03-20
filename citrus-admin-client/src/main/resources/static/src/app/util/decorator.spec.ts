import {memoize} from "./decorator";
describe('decorators', () => {

    describe('memoize', () => {

        class Test {
            counter = 0;
            @memoize()
            doSomeThing(p?:any) {
                this.counter++;
                return this.counter;
            }
        }

        let test:Test;

        beforeEach(() => test = new Test())

        it('should memoize', () => {
            expect(test.counter).toBe(0);
            test.doSomeThing()
            test.doSomeThing()
            expect(test.counter).toBe(1);
        })

    })

})