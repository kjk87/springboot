package kr.co.pplus.store.api.util;

import lombok.Data;

import java.util.Objects;

@Data
public class Error {
    private Long errcode = null;

    private String message = null;

    private String rawMessage = null;
}

