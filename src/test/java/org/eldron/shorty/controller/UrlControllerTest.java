package org.eldron.shorty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eldron.shorty.config.EmbeddedRedisConfiguration;
import org.eldron.shorty.vo.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EmbeddedRedisConfiguration.class)
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenRequestUrlThatDoesntExist_thenReturnNotFound() throws Exception {
        final var shortenedUrl = "abcd";

        mockMvc.perform(get("/shorten/" + shortenedUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenRequestToShortenUrl_thenReturnSavedUrl() throws Exception {
        final var url = "http://www.google.com";
        final var request = "{\"url\": \"" + url + "\"}";

        final var mvcResult = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains(url);
    }

    @Test
    void whenRequestToShortenUrlWithoutProtocol_thenReturnUrl() throws Exception {
        final var url = "www.google.com";
        final var request = "{\"url\": \"" + url + "\"}";

        final var mvcResult = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains("Invalid url");
    }

    @Test
    void whenRequestToShortenWithUrlThatDoesntExist_thenReturnUrl() throws Exception {
        final var url = "http://www.completely-invalid-url.com";
        final var request = "{\"url\": \"" + url + "\"}";

        final var mvcResult = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains("Invalid url");
    }

    @Test
    void whenRequestUrlExists_thenReturnOriginalUrl() throws Exception {
        final var url = "http://www.google.com";
        final var request = "{\"url\": \"" + url + "\"}";

        final var mvcPostResult = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        final var mapper = new ObjectMapper();
        final var response = mvcPostResult.getResponse().getContentAsString();
        final var responseUrl = mapper.readValue(response, Url.class);

        final var mvcResult = mockMvc.perform(get("/shorten/" + responseUrl.getShortUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains(url);
    }
}
