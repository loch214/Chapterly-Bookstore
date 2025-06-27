package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.ContactInfo;
import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.service.ContactInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactInfoService contactInfoService;

    @PostMapping("/add")
    public String addContact(@RequestParam String nickname,
                             @RequestParam String address,
                             @RequestParam String phoneNumber,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        ContactInfo contact = new ContactInfo(0, user.getId(), nickname, address, phoneNumber);
        contactInfoService.addContact(contact);
        return "redirect:/order/profile";
    }

    @PostMapping("/update")
    public String updateContact(@RequestParam int id,
                                @RequestParam String nickname,
                                @RequestParam String address,
                                @RequestParam String phoneNumber,
                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        ContactInfo contact = contactInfoService.getContactById(id);
        if (contact != null && contact.getUserId() == user.getId()) {
            contact.setNickname(nickname);
            contact.setAddress(address);
            contact.setPhoneNumber(phoneNumber);
            contactInfoService.updateContact(contact);
        }
        return "redirect:/order/profile";
    }

    @PostMapping("/delete")
    public String deleteContact(@RequestParam int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        ContactInfo contact = contactInfoService.getContactById(id);
        if (contact != null && contact.getUserId() == user.getId()) {
            contactInfoService.deleteContactById(id);
        }
        return "redirect:/order/profile";
    }
} 