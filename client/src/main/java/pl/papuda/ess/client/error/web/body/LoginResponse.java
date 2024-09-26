/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.papuda.ess.client.error.web.body;

import lombok.Data;

/**
 *
 * @author konrad
 */
@Data
public class LoginResponse {
    private Long userId;
    private String token;
}
