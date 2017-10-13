import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TestStateActions} from "./test.state";

@Component({
    templateUrl: 'tests.html'
})
export class TestsComponent implements OnInit {

    constructor(private router:Router,
                private testActions:TestStateActions
    ) {}

    ngOnInit(): void {
      this.testActions.fetchPackages();
    }

    isActive(name: string) {
        return this.router.isActive('tests/' + name, false);
    }
}
