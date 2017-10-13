import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";

@Component({
    template: `<div class="container space-top-30">
  <h1 class="page-header">About <small>Citrus administration UI</small></h1>

  <p class="lead">This is the Citrus administration UI that enables you to manage your Citrus integration testing projects.</p>
    
  <h4>Version: {{version}}</h4>
  <h4>Copyright &copy; 2017 ConSol Software GmbH</h4>

  <h4><a href="https://www.citrusframework.org">https://www.citrusframework.org</a></h4>
</div>`
})
export class AboutComponent implements OnInit {

    constructor(private http: Http) {
    }

    version: string = "";

    ngOnInit(): void {
        this.http.get("api/version")
            .map(res => res.text())
            .subscribe(version => this.version = version);
    }
}
