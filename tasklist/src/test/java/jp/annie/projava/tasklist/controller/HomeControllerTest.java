package jp.annie.projava.tasklist.controller;

import jp.annie.projava.tasklist.controller.HomeController.TaskItem;
import jp.annie.projava.tasklist.dao.TaskListDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private TaskListDao dao = mock(TaskListDao.class);

    @InjectMocks
    private HomeController target = new HomeController(dao);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(target).build();
    }

    @Test
    @DisplayName("/hello endpoint - 正常")
    void hello() throws Exception {

        // TODO LocalDateTime.now()がmodelに入っていることを確認する
        mockMvc.perform(get("/hello/"))
                .andDo(print()) // リクエスト詳細
                .andExpect(status().isOk())
                .andExpect(view().name("hello"));
    }

    @Test
    @DisplayName("/list endpoint - 正常")
    void listItems() throws Exception {

        // 登録済のタスクが0件のケース
        when(this.dao.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/list/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskList", List.of()))
                .andExpect(view().name("home"));

        // 登録済のタスクがあるケース
        var expectTaskItems = List.of(
                new TaskItem("1", "task1", "2022-05-01", false),
                new TaskItem("2", "task2", "2022-05-03", true)
        );
        when(this.dao.findAll()).thenReturn(expectTaskItems);

        mockMvc.perform(get("/list/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskList", expectTaskItems))
                .andExpect(view().name("home"));
    }

    @Test
    @DisplayName("/add endpoint - 正常")
    void addItem() throws Exception {

        mockMvc.perform(get("/add/")
                        .param("task", "task1")
                        .param("deadline", "2022-05-08"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/list"));
    }

    @Test
    @DisplayName("/add endpoint - 必須パラメータなし")
    void addItem_401() throws Exception {

        mockMvc.perform(get("/add/")
                        .param("task", "task1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/add/")
                        .param("deadline", "2022-05-08"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/add/"))
                .andExpect(status().isBadRequest());
    }

    // TODO: /delete endpointの正常系

    @Test
    @DisplayName("/delete endpoint - 必須パラメータなし")
    void deleteItem() throws Exception {

        mockMvc.perform(get("/delete"))
                .andExpect(status().isBadRequest());
    }

    // TODO: /update endpointの正常系

    @Test
    @DisplayName("/update endpoint - 必須パラメータなし")
    void updateItem() throws Exception {

        mockMvc.perform(get("/update/"))
                .andExpect(status().isBadRequest());
    }
}
