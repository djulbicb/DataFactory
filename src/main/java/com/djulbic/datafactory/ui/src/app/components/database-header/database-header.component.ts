import { Component, OnInit, Output, Input, EventEmitter, ViewChild, ElementRef, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/service/api-service.service';
import { DatabaseRequestConfig } from 'src/app/model/DatabaseRequestConfig';
import { ColumnSql } from 'src/app/model/ColumnSql';
import { MatSnackBar } from '@angular/material';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { ModalDbConnectionComponent } from '../modal-db-connection/modal-db-connection.component';
import { DbConnection } from 'src/app/model/DbConnection';
import { SnackBarService } from 'src/app/service/snack-bar-service.service';


export interface DialogData {
  animal: string;
  name: string;
}


@Component({
  selector: 'database-header',
  templateUrl: './database-header.component.html',
  styleUrls: ['./database-header.component.css']
})
export class DatabaseHeaderComponent implements OnInit {
  selectedDatabase:string = "";
  selectedLanguage:string = "ENGLISH";
  selectedConnectionPreset:DbConnection;

  @ViewChild("inputInsertCount", {static:null}) inputInsertCount:ElementRef;
  dataLibraryLanguages:Array<string>;

  inputdatabases:Observable<any>;
  databases:Array<any>;
  tables:Array<any>;
  selectedTable:string = "";
  inputColumns:Array<ColumnSql>;

  autoExecute:boolean = true;

  presetConnections:DbConnection[];

  @Output() emmitDatabaseChanged = new EventEmitter<String>();
  @Output() emmitShowColumns = new EventEmitter<DatabaseRequestConfig>();
  @Output() emmitExecute = new EventEmitter<String>();

  buttonBatchValues = [1, 5, 10, 25, 50, 100, 250, 500, 1000];

  constructor(
    private apiService:ApiService,
    private snackService:SnackBarService,
    public dialog: MatDialog
) { }

  driver: "";
  username: "";
  password: "";
  url:"";

  openDialog(): void {
    let db:DbConnection = {
      driver: "",
      username: "",
      password: "",
      url:""
    }
    const dialogRef = this.dialog.open(ModalDbConnectionComponent, {
      width: '650px',
      data: {
        driver: this.driver,
        username: this.username,
        password: this.password,
        url: this.url
      }
    });

    dialogRef.afterClosed().subscribe(dbConnectionInfo => {
      console.log('The dialog was closed');
      if(dbConnectionInfo){
        this.apiService.addNewConnection(dbConnectionInfo).subscribe((data)=>{
          this.snackService.showSuccess("Added connection");

          this.apiService.getPresetConnections().subscribe((data)=>{
            console.log(data);
            this.presetConnections = data;
          })
          
        });
      }
    });
  }

  changeConnection(event){
    console.log("Connection changed");
    console.log(this.selectedConnectionPreset);
    console.log(event);
    this.apiService.getDatabases(this.selectedConnectionPreset).subscribe((data)=>{
      this.databases = data;
    })
  }

  ngOnInit() {
    this.apiService.getDataLibraryLanguages().subscribe((data)=>{
      this.dataLibraryLanguages = data;
    });

    this.apiService.getPresetConnections().subscribe((data)=>{
      console.log(data);
      this.presetConnections = data;
    })
  }

  public getDatabaseRequestConfig(){
    let config:DatabaseRequestConfig = {
      databaseName: "",
      databaseTable: "",
      driver: this.selectedConnectionPreset.driver,
      url: this.selectedConnectionPreset.url,
      username: this.selectedConnectionPreset.username,
      password: this.selectedConnectionPreset.password,
      language: ""
    };

    config.databaseName = this.selectedDatabase;
    config.databaseTable = this.selectedTable;
    config.language = this.selectedLanguage;
    return config;
  }

  checkAndExecute(numberOfQueries){
    this.inputInsertCount.nativeElement.value = numberOfQueries;
    if(this.autoExecute){
      this.emmitExecute.emit(numberOfQueries);
    } else{
      // this._snackBar.open("Info: Auto execute is turned off.", null, {duration: 3600, panelClass:['info-snackbar']});
    }
  }

  execute(numberOfQueries){
    this.emmitExecute.emit(numberOfQueries);
  }

  createConnection(){

  }

  onDatabaseChange(){
    let config = this.getDatabaseRequestConfig();
    this.selectedTable="";
    this.emmitDatabaseChanged.emit(this.selectedDatabase);

    this.apiService.getTables(config).subscribe((data)=>{
      this.tables = data;
      console.log(data);
    })
  }

  onTableChange(){
    let config = this.getDatabaseRequestConfig();
    this.apiService.getColumns(config).subscribe((data)=>{
      console.log(data);
      this.emmitShowColumns.emit(config);
    })
  }
}
