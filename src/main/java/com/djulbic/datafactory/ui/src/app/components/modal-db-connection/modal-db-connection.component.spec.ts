import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalDbConnectionComponent } from './modal-db-connection.component';

describe('ModalDbConnectionComponent', () => {
  let component: ModalDbConnectionComponent;
  let fixture: ComponentFixture<ModalDbConnectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ModalDbConnectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalDbConnectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
