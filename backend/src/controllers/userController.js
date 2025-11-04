export const getUserProfile = (req, res) => {
    try {
        const user = req.user;
        return res.status(200).json({ user });
    } catch (error) {
        console.error('Lỗi khi lấy user profile:', error);
        return res.status(500).json({ error: 'Internal Server Error' });
    }
};

export const updateUserProfile = (req, res) => {
    const { name, email } = req.body;
    req.user.name = name;
    req.user.email = email;
    req.user.save()
        .then(() => res.status(200).json({ user: req.account }))
        .catch((error) => res.status(500).json({ error: error.message }));
};

export const deleteUserProfile = (req, res) => {
    req.account.remove()
        .then(() => res.status(200).json({ message: 'User deleted successfully' }))
        .catch((error) => res.status(500).json({ error: error.message }));
};