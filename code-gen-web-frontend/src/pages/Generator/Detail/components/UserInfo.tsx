import {Avatar, Card, Flex, Space} from 'antd';



const UserInfoTab: React.FC<API.UserVO> = (user) => {

  const { Meta } = Card;

  return (
    <Card
      hoverable
    >
      <Flex justify={"start"} >
        <Space>
          <Avatar src={user.userAvatar}></Avatar>
          <Meta title={user.userName} description={user.userProfile} />
        </Space>

      </Flex>

    </Card>
  );

}

export default UserInfoTab;
