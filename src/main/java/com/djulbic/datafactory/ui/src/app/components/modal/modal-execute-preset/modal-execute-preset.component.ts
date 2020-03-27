import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { ExecuteRequestPreset } from 'src/app/model/ExecuteRequestPreset';


@Component({
  selector: 'app-modal-execute-preset',
  templateUrl: './modal-execute-preset.component.html',
  styleUrls: ['./modal-execute-preset.component.css']
})
export class ModalExecutePresetComponent implements OnInit {

  public dialogRef: MatDialogRef<ExecuteRequestPreset>;

  constructor (@Inject(MAT_DIALOG_DATA) public data: ExecuteRequestPreset) { 

  }

  ngOnInit() {

  }

  onOkClicked(){
    
  }
  
  onCloseClicked(){
    this.dialogRef.close();
  }

}
