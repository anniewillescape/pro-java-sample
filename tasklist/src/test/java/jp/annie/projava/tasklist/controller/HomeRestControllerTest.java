package jp.annie.projava.tasklist.controller;

import jp.annie.projava.tasklist.controller.HomeRestController.TaskItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class HomeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private HomeRestController target = new HomeRestController();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(target).build();
    }

    private static final LocalDateTime now = LocalDateTime.of(2022, 5, 8, 13, 0, 0);

    private static final UUID uuid = UUID.fromString("12345678-0000-0000-0000-000000000000");

    @Test
    @DisplayName("/resthello endpoint - 正常")
    void hello() throws Exception {

        // LocalDateTime.now() をmock化
        MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class);
        mock.when(LocalDateTime::now).thenReturn(now);

        mockMvc.perform(get("/resthello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("""
                        Hello.
                        It works!
                        現在時刻は2022-05-08T13:00です。
                        """));
        mock.close();
    }

    @Test
    @DisplayName("/restadd - 正常")
    void add() throws Exception {

        // UUID.fromString() の mock化
        MockedStatic<UUID> mock = Mockito.mockStatic(UUID.class);
        mock.when(UUID::randomUUID).thenReturn(uuid);

        mockMvc.perform(get("/restadd")
                        .param("task", "task1")
                        .param("deadline", "2022-05-20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("タスクを追加しました。"));

        assertEquals(1, target.taskItems.size());
        assertEquals("12345678", target.taskItems.get(0).id());
        assertEquals("task1", target.taskItems.get(0).task());
        assertEquals("2022-05-20", target.taskItems.get(0).deadline());
        assertEquals(false, target.taskItems.get(0).done());
    }

    @Test
    @DisplayName("/restadd - 必須パラメータなし")
    void add_401() throws Exception {

        mockMvc.perform(get("/restadd")
                        .param("task", "task1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertEquals(0, target.taskItems.size());

        mockMvc.perform(get("/restadd")
                        .param("deadline", "2022-05-20"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        assertEquals(0, target.taskItems.size());
    }

    @Test
    @DisplayName("/restlist endpoint - 正常(登録0件)")
    void restItems_zero() throws Exception {

        mockMvc.perform(get("/restlist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("/restlist endpoint - 正常(登録複数件)")
    void restItems_multiple() throws Exception {

        target.taskItems = List.of(
                new TaskItem("1", "task1", "2022-05-01", false),
                new TaskItem("2", "task2", "2022-05-01", true)
        );

        mockMvc.perform(get("/restlist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("TaskItem[id=1, task=task1, deadline=2022-05-01, done=false],TaskItem[id=2, task=task2, deadline=2022-05-01, done=true]"));
    }
}
