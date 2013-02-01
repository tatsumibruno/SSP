/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.transferobject.reports;


import org.jasig.ssp.model.Person;
import org.jasig.ssp.transferobject.CoachPersonLiteTO;

public class BaseStudentReportTO {

	/**
	 * Construct a transfer object from a related model instance
	 * 
	 * @param model
	 *            Initialize instance from the data in this model
	 */
	public BaseStudentReportTO(final Person model) {	
		this.setPerson(model);

	}
	
	public BaseStudentReportTO()
	{
		
	}
	
	private String firstName;
	private String lastName;
	private String middleName;
	private String schoolId;
	private String primaryEmailAddress;
	private String secondaryEmailAddress;
	private String studentType;
	private CoachPersonLiteTO coach;
	private Person person;

	
	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getPrimaryEmailAddress() {
		return primaryEmailAddress;
	}

	public void setPrimaryEmailAddress(String primaryEmailAddress) {
		this.primaryEmailAddress = primaryEmailAddress;
	}

	public String getSecondaryEmailAddress() {
		return secondaryEmailAddress;
	}

	public void setSecondaryEmailAddress(String secondaryEmailAddress) {
		this.secondaryEmailAddress = secondaryEmailAddress;
	}

	public CoachPersonLiteTO getCoach() {
		return coach;
	}

	public void setCoach(CoachPersonLiteTO coach) {
		this.coach = coach;
	}
	
	public String getCoachName(){
		return coach.getFirstName() + " " + coach.getLastName();
	}


	public Person getPerson() {
		return person;
	}

	
	public void setPerson(Person person) {
		this.person = person;
		setFirstName(person.getFirstName());
		setLastName(person.getLastName());
		setMiddleName(person.getMiddleName());
		setPrimaryEmailAddress(person.getPrimaryEmailAddress());
		setSecondaryEmailAddress(person.getSecondaryEmailAddress());
		setSchoolId(person.getSchoolId());
		setCoach(new CoachPersonLiteTO(person.getCoach()));
		setStudentType(person.getStudentType().getName());
	}
}
