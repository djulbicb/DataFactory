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
  presets:string[];

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

  showTable(config:DatabaseRequestConfig){
    console.log("showTable()");
    this.api.getColumns(config).subscribe((data)=>{
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

  clearColumnRows(){
    this.columnRows = [];
    this.presets = [];
  }

  deletePreset(request:ExecuteRequestDTO){
    console.log(request);
  }

  onDatabaseChanged(pass_config:DatabaseRequestConfig){
    this.clearColumnRows();
    this.loadExecutePresets(pass_config);
  }

  loadExecutePresets(pass_config:DatabaseRequestConfig){
    let request: ExecuteRequestDTO = {
      config : pass_config,
      insertQount: this.header.getInsertCount(),
      columns: []
    }

    this.api.getDatabaseRequestConfigPresetsAsStringList(request).subscribe(listOfPresets=>{
      this.presets = listOfPresets;
    });
  }

  addExecuteRequestPreset(){
    const dialogRef = this.dialog.open(ModalExecutePresetComponent, {
      width: '650px',
      data: {
        presetName: ""
      }
    });

    dialogRef.afterClosed().subscribe(inputPresetName => {
      console.log('The preset dialog was closed');
      console.log(inputPresetName);

      if(inputPresetName){
        //this.api.addDatabaseRequestConfigPreset(inputPreset);

        let presetToSave = this.getExecutePresetFromUI();
        presetToSave.presetName = inputPresetName;

        console.log(presetToSave);
        this.api.addDatabaseRequestConfigPreset(presetToSave).subscribe((response)=>{
          console.log(response);
          this.loadExecutePresets(presetToSave.request.config);
        });
      }
    });
  }

  removeExecuteRequestPreset(in_presetName:string){
    let presetToRemove = this.getExecutePresetFromUI();
    presetToRemove.presetName = in_presetName;

    this.api.removeDatabaseRequestConfigPreset(presetToRemove).subscribe(res_updatededPresetsAfterRemoval=>{
      this.presets = res_updatededPresetsAfterRemoval;
    });
  }

  loadExecuteRequestPresetByPresetName(in_presetName:string){
    /* execute preset object serves as a dto here. Only in_presetName and databaseName are used on backend*/
    let presetToLoad = this.getExecutePresetFromUI();
    presetToLoad.presetName = in_presetName;
    
    this.api.getDatabaseRequestConfigPresetByPresetName(presetToLoad).subscribe(res_presetToLoad=>{
      console.log(res_presetToLoad);
      
      let execute = res_presetToLoad.request;
      this.header.setSelectedTable(execute.config.databaseTable);
      this.header.setLanguage(execute.config.language);
      this.header.setInsertCount(execute.insertQount);
      this.columnRows = execute.columns;
    });
  }

  
  
  getExecutePresetFromUI(){
    let databaseConfig = this.header.getDatabaseRequestConfig();
    let insertCount = this.header.getInsertCount();

    let execute:ExecuteRequestDTO = {
      config: databaseConfig,
      columns: this.columnRows,
      insertQount: insertCount
    }
    let preset:ExecuteRequestPreset = {
      presetName: "",
      request: execute
    }
    return preset;
  }
}
