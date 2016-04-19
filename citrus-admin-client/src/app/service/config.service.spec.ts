import {provide} from "angular2/core";
import {it, describe, expect, beforeEachProviders, inject} from "angular2/testing";
import {Response, XHRBackend, ResponseOptions, HTTP_PROVIDERS} from "angular2/http";
import {MockConnection, MockBackend} from "angular2/src/http/backends/mock_backend";
import {ConfigService} from "./config.service";
import {GlobalVariables} from "../model/global.variables";
import {NamespaceContext} from "../model/namespace.context";

describe('ConfigService', () => {
    beforeEachProviders(() => {
        return [
            HTTP_PROVIDERS,
            provide(XHRBackend, {useClass: MockBackend}),
            ConfigService
        ]
    });

    it('should get global variables', inject([XHRBackend, ConfigService], (backend, service) => {
        backend.connections.subscribe(
            (connection:MockConnection) => {
                var options = new ResponseOptions({
                    body: {
                        "variables": [{"name": "var1", "value": "test"}]
                      }
                });

                var response = new Response(options);

                connection.mockRespond(response);
            }
        );

        service.getGlobalVariables().subscribe(
            (variables: GlobalVariables) => {
                expect(variables.variables.length).toBe(1);
                expect(variables.variables[0].name).toBe('var1');
            }
        );
    }));

    it('should get namespace context', inject([XHRBackend, ConfigService], (backend, service) => {
        backend.connections.subscribe(
            (connection:MockConnection) => {
                var options = new ResponseOptions({
                    body: {
                        "namespaces": [{"prefix": "pfx1", "value": "test"}]
                    }
                });

                var response = new Response(options);

                connection.mockRespond(response);
            }
        );

        service.getNamespaceContext().subscribe(
            (context: NamespaceContext) => {
                expect(context.namespaces.length).toBe(1);
                expect(context.namespaces[0].prefix).toBe('pfx1');
            }
        );
    }));
});