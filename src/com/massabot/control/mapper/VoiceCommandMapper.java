package com.massabot.control.mapper;

import com.massabot.control.model.VoiceCommand;
import com.massabot.control.model.VoiceCommandExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VoiceCommandMapper {
    long countByExample(VoiceCommandExample example);

    int deleteByExample(VoiceCommandExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(VoiceCommand record);

    int insertSelective(VoiceCommand record);

    List<VoiceCommand> selectByExample(VoiceCommandExample example);

    VoiceCommand selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") VoiceCommand record, @Param("example") VoiceCommandExample example);

    int updateByExample(@Param("record") VoiceCommand record, @Param("example") VoiceCommandExample example);

    int updateByPrimaryKeySelective(VoiceCommand record);

    int updateByPrimaryKey(VoiceCommand record);
}