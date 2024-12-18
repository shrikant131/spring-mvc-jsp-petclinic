/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Collection;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/owners/{ownerId}")
public class PetController {

    private final ClinicService clinicService;

    @Autowired
    public PetController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @ModelAttribute("types")
    public Collection<PetType> populatePetTypes() {
        return this.clinicService.findPetTypes();
    }

    @ModelAttribute("owner")
    public Owner findOwner(@PathVariable("ownerId") int ownerId) {
        return this.clinicService.findOwnerById(ownerId);
    }

    @InitBinder("owner")
    public void initOwnerBinder(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @InitBinder("pet")
    public void initPetBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new PetValidator());
    }

    @RequestMapping(value = "/pets/new", method = RequestMethod.GET)
    public Pet initCreationForm(Owner owner) {
        Pet pet = new Pet();
        owner.addPet(pet);
        return pet;
    }

    @RequestMapping(value = "/pets/new", method = RequestMethod.POST)
    public Pet processCreationForm(Owner owner, @Valid Pet pet, BindingResult result) {
        if (StringUtils.hasLength(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null){
            result.rejectValue("name", "duplicate", "already exists");
        }
        if (result.hasErrors()) {
            return pet;
        } else {
            owner.addPet(pet);
            this.clinicService.savePet(pet);
            return pet;
        }
    }

    @RequestMapping(value = "/pets/{petId}/edit", method = RequestMethod.GET)
    public Pet initUpdateForm(@PathVariable("petId") int petId) {
        return this.clinicService.findPetById(petId);
    }

    @RequestMapping(value = "/pets/{petId}/edit", method = RequestMethod.POST)
    public Pet processUpdateForm(@Valid Pet pet, BindingResult result, Owner owner) {
        if (result.hasErrors()) {
            return pet;
        } else {
            owner.addPet(pet);
            this.clinicService.savePet(pet);
            return pet;
        }
    }

}
