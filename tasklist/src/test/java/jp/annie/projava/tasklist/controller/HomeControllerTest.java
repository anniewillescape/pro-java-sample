package jp.annie.projava.tasklist.controller;

import jp.annie.projava.tasklist.controller.HomeController.TaskItem;
import jp.annie.projava.tasklist.dao.TaskListDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private static final LocalDateTime now = LocalDateTime.of(2022, 5, 8, 13, 0, 0);

    private static final UUID uuid = UUID.fromString("12345678-0000-0000-0000-000000000000");

    @Test
    @DisplayName("/hello endpoint - 正常")
    void hello() throws Exception {

        // LocalDateTime.now() をmock化
        MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class);
        mock.when(LocalDateTime::now).thenReturn(now);

        mockMvc.perform(get("/hello/"))
                .andDo(print()) // リクエスト詳細
                .andExpect(status().isOk())
                .andExpect(model().attribute("time", now))
                .andExpect(view().name("hello"));

        mock.close();
    }

    @ParameterizedTest
    @DisplayName("/list endpoint - 正常")
    @MethodSource("taskItems")
    void listItems(List<TaskItem> taskItems) throws Exception {

        when(this.dao.findAll()).thenReturn(taskItems);

        mockMvc.perform(get("/list/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskList", taskItems))
                .andExpect(view().name("home"));

        verify(dao, times(1)).findAll();
    }

    static Stream<Arguments> taskItems() {
        return Stream.of(
                arguments(List.of()),
                arguments(List.of(
                        new TaskItem("1", "task1", "2022-05-01", false),
                        new TaskItem("2", "task2", "2022-05-03", true)
                ))
        );
    }

    @Test
    @DisplayName("/add endpoint - 正常")
    void addItem() throws Exception {

        // UUID.fromString() の mock化
        MockedStatic<UUID> mock = Mockito.mockStatic(UUID.class);
        mock.when(UUID::randomUUID).thenReturn(uuid);

        mockMvc.perform(get("/add/")
                        .param("task", "task1")
                        .param("deadline", "2022-05-08"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/list"));

        ArgumentCaptor<TaskItem> varArgs = ArgumentCaptor.forClass(TaskItem.class);
        verify(dao, times(1)).add(varArgs.capture());

        var capturedItem = varArgs.getValue();
        var expectedTaskItem = new TaskItem("12345678", "task1", "2022-05-08", false);

        assertEquals(expectedTaskItem, capturedItem);

        mock.close();
    }

    @Test
    @DisplayName("/add endpoint - 必須パラメータなし")
    void addItem_401() throws Exception {

        mockMvc.perform(get("/add/")
                        .param("task", "task1"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/add/")
                        .param("deadline", "2022-05-08"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/add/"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/delete endpoint - 正常")
    void deleteItem() throws Exception {

        mockMvc.perform(get("/delete/")
                        .param("id", "task_id_1"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/list"));

        ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);
        verify(dao, times(1)).delete(varArgs.capture());

        var capturedItem = varArgs.getValue();
        assertEquals("task_id_1", capturedItem);
    }


    @Test
    @DisplayName("/delete endpoint - 必須パラメータなし")
    void deleteItem_401() throws Exception {

        mockMvc.perform(get("/delete"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/update endpoint - 正常")
    void updateItem() throws Exception {

        var taskId = "task_id_1";
        var taskName = "task_name_1";
        var deadline = "2022-05-12";
        var done = false;

        mockMvc.perform(get("/update/")
                        .param("id", taskId)
                        .param("task", taskName)
                        .param("deadline", deadline)
                        .param("done", String.valueOf(done)))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/list"));

        var expectedTaskItem = new TaskItem(taskId, taskName, deadline, done);

        ArgumentCaptor<TaskItem> varArgs = ArgumentCaptor.forClass(TaskItem.class);
        verify(dao, times(1)).update(varArgs.capture());

        var capturedItem = varArgs.getValue();
        assertEquals(expectedTaskItem, capturedItem);
    }

    @Test
    @DisplayName("/update endpoint - 必須パラメータなし")
    void updateItem_401() throws Exception {

        mockMvc.perform(get("/update/"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
