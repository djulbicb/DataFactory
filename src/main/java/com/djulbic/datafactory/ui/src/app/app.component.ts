import { Component, OnInit, ViewChild, QueryList, ViewChildren } from '@angular/core';
import { Observable } from 'rxjs';
import { State,states } from './model/State';
import { ApiService } from './service/api-service.service';
import { ColumnSql } from './model/ColumnSql';
import { DatabaseRequestConfig } from './model/DatabaseRequestConfig';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl } from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import { DatabaseHeaderComponent } from './components/database-header/database-header.component';
import { SnackBarService } from './service/snack-bar-service.service';
import { SqlEntryRowComponent } from './components/sql-entry-row/sql-entry-row.component';
import { ExecuteRequestDTO } from './model/ExecuteRequestDTO';
import { MatDialog } from '@angular/material';
import { ExecuteRequestPreset } from './model/ExecuteRequestPreset';
import { ModalExecutePresetComponent } from './components/modal/modal-execute-preset/modal-execute-preset.component';
import { ModalDbConnectionComponent } from './components/modal/modal-db-connection/modal-db-connection.component';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  posts:Observable<any>;
  columnRows:ColumnSql[];
  mappedSQLTypesToDataLibraryMethods;
  presets:ExecuteRequestPreset[];

  @ViewChild("header", {static: null}) header:DatabaseHeaderComponent;
  @ViewChildren(SqlEntryRowComponent) sqlEntryRows:QueryList<SqlEntryRowComponent>;

  constructor(private api:ApiService, 
              private snackService:SnackBarService,
              public dialog: MatDialog
              ){
    api.getMappedSQLTypesToDataLibraryMethods().subscribe((data)=>{
      this.mappedSQLTypesToDataLibraryMethods = data;
      console.log(this.mappedSQLTypesToDataLibraryMethods);
    });
  }

  ngOnInit(){
    this.posts=this.api.getConfig();
  }

  showTable(configgg:DatabaseRequestConfig){
    let databaseConfig = this.header.getDatabaseRequestConfig();

    console.log("---------");
    console.log(databaseConfig);
    let execute:ExecuteRequestDTO = {
      config: databaseConfig,
      columns: [],
      insertQount: 10
    }
    let preset:ExecuteRequestPreset = {
      presetName: "",
      request: execute
    }
    this.api.getDatabaseRequestConfigPresets(preset).subscribe((data)=>{
      this.presets = data;
    });

    console.log("showTable()");
    this.api.getColumns(configgg).subscribe((data)=>{
      console.log(data);

      this.columnRows = data;
    });
  }

  addDatabaseRequestConfigPreset(){
    let databaseConfig = this.header.getDatabaseRequestConfig();
    let execute:ExecuteRequestDTO = {
      config : databaseConfig,
      columns: this.columnRows,
      insertQount: 10
    };
    let preset:ExecuteRequestPreset = {
      presetName: "",
      request: execute
    }
    this.api.addDatabaseRequestConfigPreset(preset).subscribe((data)=>{

    });
  }

  executeBatch(numberOfQueries){
    let invalidRows = [];
    this.sqlEntryRows.forEach(row=>{
      if(!row.isValid()){
        invalidRows.push(row.entry.name);
      }
    });

    if(invalidRows.length > 0){
      this.snackService.showError("Check following rows " + invalidRows);
      return;
    }
    

    let databaseConfig = this.header.getDatabaseRequestConfig();
    if(databaseConfig.databaseName !== "" && databaseConfig.databaseTable !==""){
      this.api.execute(databaseConfig, this.columnRows, numberOfQueries).subscribe((data)=>{
        console.log(data.message);
        this.snackService.showInfo(data.message);
      });
    } else {
      this.snackService.showInfo("Select database and table first");
    }
    
  }

  click(){
    console.log(this.columnRows);

    this.api.execute(this.header.getDatabaseRequestConfig(), this.columnRows, 1).subscribe((data)=>{
      console.log(data);
    });
  }

    clearRows(){
console.log("clear");
      this.columnRows = [];
    }

  deletePreset(request:ExecuteRequestDTO){
    console.log(request);
  }

  addExecuteRequestPreset(){
    const dialogRef = this.dialog.open(ModalExecutePresetComponent, {
      width: '650px',
      data: {
        driver: "",
        username: "this.username",
        password: "this.password",
        url: "this.url"
      }
    });

    dialogRef.afterClosed().subscribe(dbConnectionInfo => {
      console.log('The dialog was closed');
      if(dbConnectionInfo){
        
          
        
      }
    });
  }
}
