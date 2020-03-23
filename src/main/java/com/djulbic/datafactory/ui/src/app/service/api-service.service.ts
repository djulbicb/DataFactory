import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { HttpHeaders } from '@angular/common/http';


import { HttpClient } from '@angular/common/http';
import { DatabaseRequestConfig } from '../model/DatabaseRequestConfig';
import { ColumnSql } from '../model/ColumnSql';
import { ExecuteRequestDTO } from '../model/ExecuteRequestDTO';
import { DbConnection } from '../model/DbConnection';

@Injectable({
  providedIn: 'root'
})
export class ApiService {


constructor(private http:HttpClient) { }

init(){
  console.log("Startup");
}

httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
    'Authorization': 'my-auth-token'
  })
};

url =  'http://localhost:8091';
configUrl = this.url + '/api/getDataLibraryMethod';
urlGetDatabases = this.url + '/api/getDatabases';
urlGetTables = this.url + '/api/getTables';
urlgetDataLibraryLanguages = this.url + '/api/getDataLibraryLanguages';
urlGetColumns = this.url + '/api/getColumns'
urlGetMappedSQLTypesToDataLibraryMethods = this.url + '/api/getMappedSQLTypesToDataLibraryMethods';
urlExecute = this.url + '/api/execute';
urlAddNewConnection = this.url + '/api/addPresetConnection';
urlGetPresetConnection = this.url + '/api/getPresetConnections';


getPresetConnections(){
  console.log('Sending request to get connection presets');
  return this.http.get<DbConnection[]>(this.urlGetPresetConnection);
}

addNewConnection(dbConnectionInfo:DbConnection){
  console.log('Sending request to add new connection preset');
  return this.http.post(this.urlAddNewConnection, dbConnectionInfo);
}

execute(requestConfig:DatabaseRequestConfig, data:ColumnSql[], insertQount:number) :any{
  console.log("Execute");
  let request = new ExecuteRequestDTO();
  request.columns = data;
  request.config = requestConfig;
  request.insertQount = insertQount;
  console.log(request);
  return this.http.post(this.urlExecute, request);
}

getMappedSQLTypesToDataLibraryMethods(){
  return this.http.get<any>(this.urlGetMappedSQLTypesToDataLibraryMethods);
}

getConfig() {
  return this.http.get<any[]>(`${this.configUrl}`);
}

getDatabases(databaseConnectionPreset:DbConnection) {
  console.log("API SERVICE - getDatabases");
  return this.http.post<any>(`${this.urlGetDatabases}`, databaseConnectionPreset);
}

getTables(requestConfig:DatabaseRequestConfig) {
  console.log("API SERVICE - getTables");
  console.log(requestConfig);
  return this.http.post<string[]>(`${this.urlGetTables}`, requestConfig);
}

getColumns(requestConfig:DatabaseRequestConfig){
  console.log("API SERVICE - getColumns");
  console.log(requestConfig);
  return this.http.post<ColumnSql[]>(`${this.urlGetColumns}`, requestConfig);
}

getDataLibraryLanguages(){
  return this.http.get<any[]>(`${this.urlgetDataLibraryLanguages}`);
}

private handleError(error: HttpErrorResponse) {
  if (error.error instanceof ErrorEvent) {
    // A client-side or network error occurred. Handle it accordingly.
    console.error('An error occurred:', error.error.message);
  } else {
    // The backend returned an unsuccessful response code.
    // The response body may contain clues as to what went wrong,
    console.error(
      `Backend returned code ${error.status}, ` +
      `body was: ${error.error}`);
  }
  // return an observable with a user-facing error message
  return throwError(
    'Something bad happened; please try again later.');
};




}
