import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material';
import { ApiService } from 'src/app/service/api-service.service';
import { ExecuteRequestPreset } from 'src/app/model/ExecuteRequestPreset';
import { SnackBarService } from 'src/app/service/snack-bar-service.service';


@Component({
  selector: 'app-modal-execute-preset',
  templateUrl: './modal-execute-preset.component.html',
  styleUrls: ['./modal-execute-preset.component.css']
})
export class ModalExecutePresetComponent implements OnInit {
  existingPresets:string[];

  constructor(
    private apiService:ApiService,
    private snackService: SnackBarService,
    public dialogRef: MatDialogRef<string>,
    @Inject(MAT_DIALOG_DATA) public data: ExecuteRequestPreset) { }

  ngOnInit() {
    this.data.presetName = this.data.presetName ? this.data.presetName : "";
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  verifyAndSend(data){
    console.log(data);
    if(data.presetName){
      this.dialogRef.close(data.presetName);
    } else{
      this.snackService.showError("Enter preset name");
    }
  }


}