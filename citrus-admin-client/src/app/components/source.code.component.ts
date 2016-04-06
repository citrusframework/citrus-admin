import {Component,  Input, OnChanges, AfterViewInit} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";

declare var ace;

@Component({
    selector: "source-code",
    template: '<pre id="{{id}}" class="code-editor">{{sourceCode}}</pre>'
})
export class SourceCodeComponent implements OnChanges, AfterViewInit {
    @Input('editor-id') id: string;
    @Input() type = "xml";
    @Input() detail: TestDetail;

    constructor(private _testService: TestService) {}

    errorMessage: string;
    sourceCode = 'Loading sources ...';
    editor: any;

    ngOnChanges() {
        this.getSourceCode();
    }

    ngAfterViewInit() {
        this.editor = ace.edit(this.id);
        this.editor.setTheme("ace/theme/chrome");
        this.editor.session.setMode("ace/mode/" + this.type);
    }

    getSourceCode() {
        this._testService.getSourceCode(this.detail, this.type)
            .subscribe(
                sourceCode => {
                    this.sourceCode = sourceCode;

                    if (this.editor) {
                        this.editor.setValue(this.sourceCode, 1);
                        this.editor.resize();
                    }
                },
                error => this.errorMessage = <any>error);
    }
}