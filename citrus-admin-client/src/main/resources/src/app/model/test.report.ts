import {TestResult} from "./tests";
export class TestReport {

    constructor() {}

    public projectName: string;
    public suiteName: string;
    public executionDate: number;
    public duration: number = 0;
    public total: number = 0;
    public passed: number = 0;
    public failed: number = 0;
    public skipped: number = 0;

    public results: TestResult[] = [];
}