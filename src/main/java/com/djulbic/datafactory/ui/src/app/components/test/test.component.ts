import { Component, OnInit, Input } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  @Input() data:Observable<any[]>;
  testData:any[] = []

  constructor() { }

  ngOnInit() {
    this.data.subscribe(val=>{
      console.log(val);
      this.testData = val;
    });
  }

}
