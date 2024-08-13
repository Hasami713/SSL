package com.example.ssl.api;

import com.example.ssl.model.*;

import java.util.List;

public interface ParserApi {
     LaundriesInfo getLaundries();
     Laundry getLaundry(String laundryId);
     LaundryInfo getLaundryInfo(String laundryId);
     Boolean checkAvailable(String laundryId, String slotId);
     List<Slot> getAvailable(String laundryId);
}