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
import { SnackBarService } from './snack-bar-service.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {


constructor(
  private http:HttpClient, 
  private snack:SnackBarService) { }

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
  return this.http.get<DbConnection[]>(this.urlGetPresetConnection).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

addNewConnection(dbConnectionInfo:DbConnection){
  console.log('Sending request to add new connection preset');
  return this.http.post(this.urlAddNewConnection, dbConnectionInfo).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

execute(requestConfig:DatabaseRequestConfig, data:ColumnSql[], insertQount:number) :any{
  console.log("Execute");
  let request = new ExecuteRequestDTO();
  request.columns = data;
  request.config = requestConfig;
  request.insertQount = insertQount;
  console.log(request);
  return this.http.post(this.urlExecute, request).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

getMappedSQLTypesToDataLibraryMethods(){
  return this.http.get<any>(this.urlGetMappedSQLTypesToDataLibraryMethods).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

getConfig() {
  return this.http.get<any[]>(`${this.configUrl}`).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

getDatabases(databaseConnectionPreset:DbConnection) {
  console.log("API SERVICE - getDatabases");
  return this.http.post<any>(`${this.urlGetDatabases}`, databaseConnectionPreset).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );
}

getTables(requestConfig:DatabaseRequestConfig) {
  console.log("API SERVICE - getTables");
  console.log(requestConfig);
  return this.http.post<string[]>(`${this.urlGetTables}`, requestConfig).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

getColumns(requestConfig:DatabaseRequestConfig){
  console.log("API SERVICE - getColumns");
  console.log(requestConfig);
  return this.http.post<ColumnSql[]>(`${this.urlGetColumns}`, requestConfig).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );;
}

getDataLibraryLanguages(){
  return this.http.get<any[]>(`${this.urlgetDataLibraryLanguages}`).pipe(
    catchError((e)=>this.handleError(e, this.snack))
  );
}

private handleError(error: HttpErrorResponse, snack:SnackBarService) {
  if (error.error instanceof ErrorEvent) {
    // A client-side or network error occurred. Handle it accordingly.
    console.error('Bojan - An error occurred:', error.error.message);
  } else {
    // The backend returned an unsuccessful response code.
    // The response body may contain clues as to what went wrong,
    console.error(
      `Bojan - Backend returned code ${error.status}, ` +
      `body was: ${error.error}`);
  }
  
  let trace = error.error.trace;
  let message = trace.substring(0, trace.indexOf("\r\n"));;
  snack.showError(message);
  // return an observable with a user-facing error message
  return throwError(
    'Bojan - Something bad happened; please try again later.');
};




}
